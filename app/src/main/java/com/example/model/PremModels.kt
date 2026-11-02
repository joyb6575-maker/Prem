package com.example.model

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val age: Int = 22,
    val gender: String = "Female", // "Male", "Female", "Other"
    val division: String = "Dhaka",
    val district: String = "Dhaka",
    val city: String = "Gulshan",
    val education: String = "",
    val profession: String = "",
    val relationshipStatus: String = "Single", // "Single", "Never Married", "Divorced"
    val interests: List<String> = emptyList(),
    val hobbies: List<String> = emptyList(),
    val bio: String = "",
    val partnerPreferences: String = "",
    val profilePhotoUrl: String = "",
    val photos: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val isBanned: Boolean = false,
    val isHidden: Boolean = false,
    val isPremium: Boolean = false,
    val role: String = "user", // "user" or "admin"
    val isOnline: Boolean = true,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

data class UserLike(
    val fromUserId: String = "",
    val toUserId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isMatch: Boolean = false,
    val senderProfile: UserProfile? = null
)

data class MatchItem(
    val id: String = "",
    val users: List<String> = emptyList(),
    val user1Id: String = "",
    val user2Id: String = "",
    val otherUser: UserProfile? = null,
    val lastMessage: String = "",
    val lastMessageSenderId: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false
)

data class ChatMessage(
    val id: String = "",
    val matchId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)

data class SafetyReport(
    val id: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val reason: String = "",
    val details: String = "",
    val status: String = "pending", // "pending", "reviewed", "resolved"
    val createdAt: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val type: String = "like", // "like", "match", "message", "verification", "security"
    val title: String = "",
    val body: String = "",
    val fromUserId: String = "",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AppLanguage {
    BN, // বাংলা
    EN  // English
}
