package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PremRepository
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.components.FullProfileDetailDialog
import com.example.ui.components.MatchDialog
import com.example.ui.components.PremTopAppBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.LikesScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PremiumMembershipScreen
import com.example.ui.screens.ProfileDashboardScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.SafetyCenterScreen
import com.example.ui.screens.WelcomeAuthScreen
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import com.example.ui.theme.PremTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: PremRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = PremRepository(this)

        setContent {
            PremTheme {
                PremApp(repository = repository)
            }
        }
    }
}

sealed class SubScreen {
    data object None : SubScreen()
    data class Chat(val matchId: String, val otherUser: UserProfile) : SubScreen()
    data object EditProfile : SubScreen()
    data object Admin : SubScreen()
    data object Safety : SubScreen()
    data object Premium : SubScreen()
    data object Notifications : SubScreen()
}

@Composable
fun PremApp(repository: PremRepository) {
    var language by remember { mutableStateOf(AppLanguage.BN) }
    var authUser by remember { mutableStateOf(repository.auth.currentUser) }

    LaunchedEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { fbAuth ->
            authUser = fbAuth.currentUser
        }
        repository.auth.addAuthStateListener(authStateListener)
    }

    val currentUserProfile by repository.currentUserProfile.collectAsState()

    // Matching popup state
    var matchedUserPopup by remember { mutableStateOf<UserProfile?>(null) }
    var selectedProfileForDetail by remember { mutableStateOf<UserProfile?>(null) }

    // Navigation sub-screen state
    var currentSubScreen by remember { mutableStateOf<SubScreen>(SubScreen.None) }

    // Bottom navigation index (0: Discover, 1: Likes, 2: Matches, 3: Messages, 4: Profile)
    var currentBottomTab by remember { mutableIntStateOf(0) }

    // Listen for mutual match events
    LaunchedEffect(Unit) {
        repository.newMatchEvent.collectLatest { matchedUser ->
            matchedUserPopup = matchedUser
        }
    }

    fun toggleLanguage() {
        language = if (language == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN
    }

    if (authUser == null) {
        // Welcome & Authentication Gate
        WelcomeAuthScreen(
            language = language,
            onLanguageToggle = { toggleLanguage() },
            onAuthSuccess = { uid ->
                // Refresh discover
                repository.loadDiscoverProfiles()
            }
        )
    } else if (currentUserProfile == null) {
        // Profile Setup Flow for new user
        ProfileSetupScreen(
            repository = repository,
            initialProfile = null,
            language = language,
            onLanguageToggle = { toggleLanguage() },
            onProfileSaved = {
                // Profile created
            }
        )
    } else {
        // Main Application Flow
        BackHandler(enabled = currentSubScreen !is SubScreen.None) {
            currentSubScreen = SubScreen.None
        }

        when (val screen = currentSubScreen) {
            is SubScreen.Chat -> {
                ChatScreen(
                    matchId = screen.matchId,
                    otherUser = screen.otherUser,
                    repository = repository,
                    language = language,
                    onBack = { currentSubScreen = SubScreen.None },
                    onViewProfile = { profile -> selectedProfileForDetail = profile }
                )
            }
            is SubScreen.EditProfile -> {
                ProfileSetupScreen(
                    repository = repository,
                    initialProfile = currentUserProfile,
                    language = language,
                    onLanguageToggle = { toggleLanguage() },
                    onProfileSaved = { currentSubScreen = SubScreen.None }
                )
            }
            is SubScreen.Admin -> {
                AdminScreen(
                    repository = repository,
                    language = language,
                    onBack = { currentSubScreen = SubScreen.None }
                )
            }
            is SubScreen.Safety -> {
                SafetyCenterScreen(
                    repository = repository,
                    language = language,
                    onBack = { currentSubScreen = SubScreen.None }
                )
            }
            is SubScreen.Premium -> {
                PremiumMembershipScreen(
                    language = language,
                    onBack = { currentSubScreen = SubScreen.None }
                )
            }
            is SubScreen.Notifications -> {
                NotificationsScreen(
                    repository = repository,
                    language = language,
                    onBack = { currentSubScreen = SubScreen.None }
                )
            }
            SubScreen.None -> {
                val notifications by repository.notifications.collectAsState()
                val unreadNotifs = notifications.count { !it.read }

                Scaffold(
                    topBar = {
                        PremTopAppBar(
                            title = when (currentBottomTab) {
                                0 -> PremStrings.get("app_name", language)
                                1 -> PremStrings.get("nav_likes", language)
                                2 -> PremStrings.get("nav_matches", language)
                                3 -> PremStrings.get("nav_messages", language)
                                4 -> PremStrings.get("nav_profile", language)
                                else -> PremStrings.get("app_name", language)
                            },
                            language = language,
                            onLanguageToggle = { toggleLanguage() },
                            unreadNotificationCount = unreadNotifs,
                            onNotificationClick = { currentSubScreen = SubScreen.Notifications }
                        )
                    },
                    bottomBar = {
                        val matches by repository.matches.collectAsState()
                        val likesReceived by repository.likesReceived.collectAsState()

                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = PremPinkPrimary,
                            tonalElevation = 8.dp
                        ) {
                            // 1. Discover
                            NavigationBarItem(
                                selected = currentBottomTab == 0,
                                onClick = { currentBottomTab = 0 },
                                icon = { Icon(Icons.Filled.Explore, contentDescription = "Discover") },
                                label = { Text(PremStrings.get("nav_discover", language), fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PremPinkPrimary,
                                    selectedTextColor = PremPinkPrimary,
                                    indicatorColor = PremPinkPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_item_discover")
                            )

                            // 2. Likes
                            NavigationBarItem(
                                selected = currentBottomTab == 1,
                                onClick = { currentBottomTab = 1 },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (likesReceived.isNotEmpty()) {
                                                Badge(containerColor = PremPinkPrimary) {
                                                    Text(likesReceived.size.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Filled.Favorite, contentDescription = "Likes")
                                    }
                                },
                                label = { Text(PremStrings.get("nav_likes", language), fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PremPinkPrimary,
                                    selectedTextColor = PremPinkPrimary,
                                    indicatorColor = PremPinkPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_item_likes")
                            )

                            // 3. Matches
                            NavigationBarItem(
                                selected = currentBottomTab == 2,
                                onClick = { currentBottomTab = 2 },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (matches.isNotEmpty()) {
                                                Badge(containerColor = PremPurpleSecondary) {
                                                    Text(matches.size.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Filled.People, contentDescription = "Matches")
                                    }
                                },
                                label = { Text(PremStrings.get("nav_matches", language), fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PremPinkPrimary,
                                    selectedTextColor = PremPinkPrimary,
                                    indicatorColor = PremPinkPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_item_matches")
                            )

                            // 4. Messages
                            NavigationBarItem(
                                selected = currentBottomTab == 3,
                                onClick = { currentBottomTab = 3 },
                                icon = { Icon(Icons.Filled.ChatBubble, contentDescription = "Messages") },
                                label = { Text(PremStrings.get("nav_messages", language), fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PremPinkPrimary,
                                    selectedTextColor = PremPinkPrimary,
                                    indicatorColor = PremPinkPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_item_messages")
                            )

                            // 5. Profile
                            NavigationBarItem(
                                selected = currentBottomTab == 4,
                                onClick = { currentBottomTab = 4 },
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                                label = { Text(PremStrings.get("nav_profile", language), fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PremPinkPrimary,
                                    selectedTextColor = PremPinkPrimary,
                                    indicatorColor = PremPinkPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_item_profile")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentBottomTab) {
                            0 -> DiscoverScreen(
                                repository = repository,
                                language = language,
                                onNavigateToChat = { matchId, otherUser ->
                                    currentSubScreen = SubScreen.Chat(matchId, otherUser)
                                }
                            )
                            1 -> LikesScreen(
                                repository = repository,
                                language = language,
                                onViewProfile = { profile -> selectedProfileForDetail = profile },
                                onUpgradeClick = { currentSubScreen = SubScreen.Premium }
                            )
                            2 -> MatchesScreen(
                                repository = repository,
                                language = language,
                                onSelectMatch = { matchId, otherUser ->
                                    currentSubScreen = SubScreen.Chat(matchId, otherUser)
                                }
                            )
                            3 -> MatchesScreen(
                                repository = repository,
                                language = language,
                                onSelectMatch = { matchId, otherUser ->
                                    currentSubScreen = SubScreen.Chat(matchId, otherUser)
                                }
                            )
                            4 -> ProfileDashboardScreen(
                                repository = repository,
                                language = language,
                                onEditProfile = { currentSubScreen = SubScreen.EditProfile },
                                onOpenSafety = { currentSubScreen = SubScreen.Safety },
                                onOpenPremium = { currentSubScreen = SubScreen.Premium },
                                onOpenAdmin = { currentSubScreen = SubScreen.Admin }
                            )
                        }
                    }
                }
            }
        }
    }

    // Mutual Match Animation Modal
    if (matchedUserPopup != null) {
        val target = matchedUserPopup!!
        MatchDialog(
            matchedUser = target,
            currentUser = currentUserProfile,
            language = language,
            onDismiss = { matchedUserPopup = null },
            onStartChat = {
                val currentUid = repository.auth.currentUser?.uid ?: ""
                val matchId = if (currentUid < target.uid) "${currentUid}_${target.uid}" else "${target.uid}_${currentUid}"
                matchedUserPopup = null
                currentSubScreen = SubScreen.Chat(matchId, target)
            }
        )
    }

    // Detail Profile Viewer
    if (selectedProfileForDetail != null) {
        val profile = selectedProfileForDetail!!
        val coroutineScope = rememberCoroutineScope()
        FullProfileDetailDialog(
            user = profile,
            language = language,
            onDismiss = { selectedProfileForDetail = null },
            onLike = {
                selectedProfileForDetail = null
                coroutineScope.launch { repository.likeUser(profile) }
            },
            onPass = {
                selectedProfileForDetail = null
                repository.passUser(profile)
            },
            onReport = {
                selectedProfileForDetail = null
                coroutineScope.launch {
                    repository.reportUser(profile.uid, profile.fullName, "Inappropriate behavior", "")
                }
            },
            onBlock = {
                selectedProfileForDetail = null
                repository.blockUser(profile.uid)
            }
        )
    }
}
