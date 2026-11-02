package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.AppNotification
import com.example.model.ChatMessage
import com.example.model.MatchItem
import com.example.model.SafetyReport
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "PremRepository"

class PremRepository(
    private val db: FirebaseFirestore,
    val auth: FirebaseAuth = Firebase.auth
) {
    // Secondary constructor resolving custom databaseId from strings.xml
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Current logged-in user profile
    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    // Discovery feed profiles
    private val _discoverProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val discoverProfiles: StateFlow<List<UserProfile>> = _discoverProfiles.asStateFlow()

    // Mutual matches
    private val _matches = MutableStateFlow<List<MatchItem>>(emptyList())
    val matches: StateFlow<List<MatchItem>> = _matches.asStateFlow()

    // Incoming likes (People who liked current user)
    private val _likesReceived = MutableStateFlow<List<UserProfile>>(emptyList())
    val likesReceived: StateFlow<List<UserProfile>> = _likesReceived.asStateFlow()

    // Outgoing likes (People current user liked)
    private val _likesSent = MutableStateFlow<List<UserProfile>>(emptyList())
    val likesSent: StateFlow<List<UserProfile>> = _likesSent.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Blocked user IDs
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    // Admin: all users and reports
    private val _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    private val _reports = MutableStateFlow<List<SafetyReport>>(emptyList())
    val reports: StateFlow<List<SafetyReport>> = _reports.asStateFlow()

    // Match animation trigger event
    private val _newMatchEvent = MutableSharedFlow<UserProfile>()
    val newMatchEvent: SharedFlow<UserProfile> = _newMatchEvent.asSharedFlow()

    private var profileListener: ListenerRegistration? = null
    private var matchesListener: ListenerRegistration? = null
    private var notificationsListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
                _currentUserProfile.value = null
                _matches.value = emptyList()
                _likesReceived.value = emptyList()
                _likesSent.value = emptyList()
                _notifications.value = emptyList()
            }
        }
    }

    private fun startListening(userId: String) {
        // Listen to current user document
        profileListener?.remove()
        profileListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to user profile", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    _currentUserProfile.value = profile
                    // Refresh feeds once current user is loaded
                    loadDiscoverProfiles()
                    loadLikes()
                    if (profile?.role == "admin") {
                        loadAdminData()
                    }
                } else {
                    // Profile not yet created in Firestore
                    _currentUserProfile.value = null
                }
            }

        // Listen to matches
        matchesListener?.remove()
        matchesListener = db.collection("matches")
            .whereArrayContains("users", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to matches", error)
                    return@addSnapshotListener
                }
                repositoryScope.launch {
                    val list = mutableListOf<MatchItem>()
                    snapshot?.documents?.forEach { doc ->
                        val item = doc.toObject(MatchItem::class.java)
                        if (item != null) {
                            val otherUid = item.users.firstOrNull { it != userId }
                            val otherUser = otherUid?.let { fetchUserProfile(it) }
                            list.add(item.copy(otherUser = otherUser))
                        }
                    }
                    _matches.value = list.sortedByDescending { it.lastMessageTimestamp }
                }
            }

        // Listen to notifications
        notificationsListener?.remove()
        notificationsListener = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to notifications", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(AppNotification::class.java) } ?: emptyList()
                _notifications.value = list.sortedByDescending { it.createdAt }
            }
    }

    private fun stopListening() {
        profileListener?.remove()
        matchesListener?.remove()
        notificationsListener?.remove()
    }

    fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("User must be authenticated")
    }

    // Google Sign-In or Direct Session initialization
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val uid = requireUserId()
            if (profile.age < 18) {
                return Result.failure(IllegalArgumentException("PREM is strictly for 18+ individuals"))
            }
            val finalProfile = profile.copy(
                uid = uid,
                lastActiveAt = System.currentTimeMillis()
            )
            db.collection("users").document(uid).set(finalProfile).await()
            _currentUserProfile.value = finalProfile
            loadDiscoverProfiles()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile", e)
            Result.failure(e)
        }
    }

    suspend fun updateIncognito(isHidden: Boolean): Result<Unit> {
        return try {
            val uid = requireUserId()
            db.collection("users").document(uid).update("hidden", isHidden).await()
            _currentUserProfile.value = _currentUserProfile.value?.copy(isHidden = isHidden)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val uid = requireUserId()
            db.collection("users").document(uid).delete().await()
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserProfile(uid: String): UserProfile? {
        return try {
            // First check seed profiles if not found in db
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                doc.toObject(UserProfile::class.java)
            } else {
                BangladeshiData.seedProfiles.find { it.uid == uid }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user $uid", e)
            BangladeshiData.seedProfiles.find { it.uid == uid }
        }
    }

    fun loadDiscoverProfiles() {
        repositoryScope.launch {
            try {
                val currentUid = auth.currentUser?.uid ?: ""
                val snapshot = db.collection("users")
                    .whereEqualTo("banned", false)
                    .whereEqualTo("hidden", false)
                    .get().await()

                val firestoreUsers = snapshot.documents
                    .mapNotNull { it.toObject(UserProfile::class.java) }
                    .filter { it.uid != currentUid && it.uid !in _blockedUserIds.value }

                // Merge with seed profiles so discovery is rich with Bangladeshi matches
                val allCandidateUsers = (firestoreUsers + BangladeshiData.seedProfiles)
                    .distinctBy { it.uid }
                    .filter { it.uid != currentUid && it.uid !in _blockedUserIds.value }

                _discoverProfiles.value = allCandidateUsers
            } catch (e: Exception) {
                Log.e(TAG, "Error loading discover profiles", e)
                _discoverProfiles.value = BangladeshiData.seedProfiles
            }
        }
    }

    fun loadLikes() {
        repositoryScope.launch {
            try {
                val currentUid = auth.currentUser?.uid ?: return@launch
                // Received likes
                val receivedSnap = db.collection("likes")
                    .whereEqualTo("toUserId", currentUid)
                    .get().await()
                val receivedProfiles = mutableListOf<UserProfile>()
                receivedSnap.documents.forEach { doc ->
                    val fromId = doc.getString("fromUserId")
                    if (fromId != null) {
                        fetchUserProfile(fromId)?.let { receivedProfiles.add(it) }
                    }
                }
                _likesReceived.value = receivedProfiles

                // Sent likes
                val sentSnap = db.collection("likes")
                    .whereEqualTo("fromUserId", currentUid)
                    .get().await()
                val sentProfiles = mutableListOf<UserProfile>()
                sentSnap.documents.forEach { doc ->
                    val toId = doc.getString("toUserId")
                    if (toId != null) {
                        fetchUserProfile(toId)?.let { sentProfiles.add(it) }
                    }
                }
                _likesSent.value = sentProfiles
            } catch (e: Exception) {
                Log.e(TAG, "Error loading likes", e)
            }
        }
    }

    suspend fun likeUser(targetUser: UserProfile): Boolean {
        val currentUid = requireUserId()
        val currentProfile = _currentUserProfile.value ?: return false

        try {
            // Save like: currentUid -> targetUser.uid
            val likeData = hashMapOf(
                "fromUserId" to currentUid,
                "toUserId" to targetUser.uid,
                "createdAt" to System.currentTimeMillis()
            )
            val likeDocId = "${currentUid}_${targetUser.uid}"
            db.collection("likes").document(likeDocId).set(likeData).await()

            // Check if targetUser liked currentUid previously
            val mutualLikeDocId = "${targetUser.uid}_${currentUid}"
            val mutualDoc = db.collection("likes").document(mutualLikeDocId).get().await()

            val isMutual = mutualDoc.exists() || targetUser.uid.startsWith("seed_user_")
            if (isMutual) {
                // It's a match!
                val matchId = if (currentUid < targetUser.uid) "${currentUid}_${targetUser.uid}" else "${targetUser.uid}_${currentUid}"
                val matchData = hashMapOf(
                    "id" to matchId,
                    "users" to listOf(currentUid, targetUser.uid),
                    "user1Id" to currentUid,
                    "user2Id" to targetUser.uid,
                    "createdAt" to System.currentTimeMillis(),
                    "lastMessage" to "Matched on PREM ❤️",
                    "lastMessageSenderId" to currentUid,
                    "lastMessageTimestamp" to System.currentTimeMillis()
                )
                db.collection("matches").document(matchId).set(matchData).await()

                // Send notification
                val notification = AppNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = currentUid,
                    type = "match",
                    title = "New Match! / নতুন ম্যাচ! ❤️",
                    body = "You and ${targetUser.fullName} liked each other!",
                    fromUserId = targetUser.uid,
                    createdAt = System.currentTimeMillis()
                )
                db.collection("notifications").document(notification.id).set(notification).await()

                // Trigger match animation
                _newMatchEvent.emit(targetUser)
                loadLikes()
                return true
            } else {
                // Notify the other user (if real)
                val notification = AppNotification(
                    id = "notif_${System.currentTimeMillis()}",
                    userId = targetUser.uid,
                    type = "like",
                    title = "Someone liked you! / নতুন লাইক!",
                    body = "${currentProfile.fullName} liked your profile.",
                    fromUserId = currentUid,
                    createdAt = System.currentTimeMillis()
                )
                db.collection("notifications").document(notification.id).set(notification).await()
                loadLikes()
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error liking user", e)
            return false
        }
    }

    fun passUser(targetUser: UserProfile) {
        _discoverProfiles.value = _discoverProfiles.value.filter { it.uid != targetUser.uid }
    }

    // Real-time Chat
    fun observeMessages(matchId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = db.collection("matches").document(matchId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing messages", error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(matchId: String, receiverId: String, text: String, imageUrl: String = ""): Result<Unit> {
        return try {
            val senderId = requireUserId()
            val msgId = "msg_${System.currentTimeMillis()}"
            val message = ChatMessage(
                id = msgId,
                matchId = matchId,
                senderId = senderId,
                receiverId = receiverId,
                text = text,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis(),
                read = false
            )
            db.collection("matches").document(matchId).collection("messages").document(msgId).set(message).await()
            // Update last message in match
            db.collection("matches").document(matchId).update(
                mapOf(
                    "lastMessage" to if (text.isNotBlank()) text else "📷 Photo",
                    "lastMessageSenderId" to senderId,
                    "lastMessageTimestamp" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            Result.failure(e)
        }
    }

    // Safety: Block & Report
    fun blockUser(userId: String) {
        val set = _blockedUserIds.value.toMutableSet()
        set.add(userId)
        _blockedUserIds.value = set
        _discoverProfiles.value = _discoverProfiles.value.filter { it.uid != userId }
        _matches.value = _matches.value.filter { it.otherUser?.uid != userId }
    }

    fun unblockUser(userId: String) {
        val set = _blockedUserIds.value.toMutableSet()
        set.remove(userId)
        _blockedUserIds.value = set
        loadDiscoverProfiles()
    }

    suspend fun reportUser(
        reportedUserId: String,
        reportedUserName: String,
        reason: String,
        details: String
    ): Result<Unit> {
        return try {
            val reporterId = requireUserId()
            val reporterName = _currentUserProfile.value?.fullName ?: "User"
            val reportId = "rep_${System.currentTimeMillis()}"
            val report = SafetyReport(
                id = reportId,
                reporterId = reporterId,
                reporterName = reporterName,
                reportedUserId = reportedUserId,
                reportedUserName = reportedUserName,
                reason = reason,
                details = details,
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            db.collection("reports").document(reportId).set(report).await()
            // Also automatically block the reported user for safety
            blockUser(reportedUserId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin Operations
    fun loadAdminData() {
        repositoryScope.launch {
            try {
                val usersSnap = db.collection("users").get().await()
                val users = usersSnap.documents.mapNotNull { it.toObject(UserProfile::class.java) }
                _allUsers.value = (users + BangladeshiData.seedProfiles).distinctBy { it.uid }

                val reportsSnap = db.collection("reports").get().await()
                val repList = reportsSnap.documents.mapNotNull { it.toObject(SafetyReport::class.java) }
                _reports.value = repList.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading admin data", e)
            }
        }
    }

    suspend fun toggleVerifyUser(userId: String, isVerified: Boolean) {
        try {
            db.collection("users").document(userId).update("verified", isVerified).await()
            _allUsers.value = _allUsers.value.map {
                if (it.uid == userId) it.copy(isVerified = isVerified) else it
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying user", e)
        }
    }

    suspend fun toggleBanUser(userId: String, isBanned: Boolean) {
        try {
            db.collection("users").document(userId).update("banned", isBanned).await()
            _allUsers.value = _allUsers.value.map {
                if (it.uid == userId) it.copy(isBanned = isBanned) else it
            }
            if (isBanned) {
                _discoverProfiles.value = _discoverProfiles.value.filter { it.uid != userId }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error banning user", e)
        }
    }

    suspend fun resolveReport(reportId: String) {
        try {
            db.collection("reports").document(reportId).update("status", "resolved").await()
            _reports.value = _reports.value.map {
                if (it.id == reportId) it.copy(status = "resolved") else it
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving report", e)
        }
    }
}
