package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BangladeshiData
import com.example.data.PremRepository
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.components.FullProfileDetailDialog
import com.example.ui.components.ReportDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import com.example.ui.theme.PremRoseTertiary
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    repository: PremRepository,
    language: AppLanguage,
    onNavigateToChat: (matchId: String, otherUser: UserProfile) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allProfiles by repository.discoverProfiles.collectAsState()
    val currentUser by repository.currentUserProfile.collectAsState()

    // Filters
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedGenderFilter by remember { mutableStateOf("All") }
    var selectedDivisionFilter by remember { mutableStateOf("All") }
    var minAgeFilter by remember { mutableStateOf(18f) }
    var maxAgeFilter by remember { mutableStateOf(45f) }

    // Detailed Profile Dialog
    var selectedDetailUser by remember { mutableStateOf<UserProfile?>(null) }
    var reportUserTarget by remember { mutableStateOf<UserProfile?>(null) }

    val filteredProfiles = remember(allProfiles, selectedGenderFilter, selectedDivisionFilter, minAgeFilter, maxAgeFilter) {
        allProfiles.filter { p ->
            (selectedGenderFilter == "All" || p.gender.equals(selectedGenderFilter, ignoreCase = true)) &&
            (selectedDivisionFilter == "All" || p.division.equals(selectedDivisionFilter, ignoreCase = true)) &&
            (p.age >= minAgeFilter.toInt() && p.age <= maxAgeFilter.toInt())
        }
    }

    // Active candidate profile at top of deck
    var currentIndex by remember { mutableStateOf(0) }
    val currentCardUser = if (filteredProfiles.isNotEmpty() && currentIndex < filteredProfiles.size) {
        filteredProfiles[currentIndex]
    } else null

    fun handleLike(user: UserProfile) {
        coroutineScope.launch {
            val isMatch = repository.likeUser(user)
            if (currentIndex < filteredProfiles.size) {
                currentIndex++
            }
        }
    }

    fun handlePass(user: UserProfile) {
        repository.passUser(user)
        if (currentIndex < filteredProfiles.size) {
            currentIndex++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = PremStrings.get("find_someone", language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { showFilterDialog = true }
                        .testTag("filter_dialog_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.FilterList,
                            contentDescription = "Filter",
                            tint = PremPinkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = PremStrings.get("filter", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PremPinkPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Discover Card Deck
            if (currentCardUser != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { selectedDetailUser = currentCardUser }
                        .testTag("discover_user_card"),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Profile Image
                        AsyncImage(
                            model = currentCardUser.profilePhotoUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                            contentDescription = currentCardUser.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Online / Recently active badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (currentCardUser.isOnline) Color(0xFF00E676) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentCardUser.isOnline) PremStrings.get("active_now", language) else PremStrings.get("recently_active", language),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Info Button
                        IconButton(
                            onClick = { selectedDetailUser = currentCardUser },
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = "View Details", tint = Color.White)
                        }

                        // Gradient Shadow & Bio Details at bottom of card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${currentCardUser.fullName}, ${currentCardUser.age}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                VerifiedBadge(isVerified = currentCardUser.isVerified)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Profession & Location
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Work, contentDescription = null, tint = PremRoseTertiary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentCardUser.profession,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentCardUser.city}, ${currentCardUser.division}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                            }

                            if (currentCardUser.bio.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentCardUser.bio,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Interests chips
                            if (currentCardUser.interests.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    currentCardUser.interests.take(3).forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = tag,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons (Pass, Super Like, Like)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pass Button
                    FloatingActionButton(
                        onClick = { handlePass(currentCardUser) },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = Color.Red.copy(alpha = 0.8f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(58.dp)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                            .testTag("action_pass_btn")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Pass", modifier = Modifier.size(28.dp))
                    }

                    // Super Like
                    FloatingActionButton(
                        onClick = { handleLike(currentCardUser) },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = Color(0xFF00B0FF),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f), CircleShape)
                            .testTag("action_super_like_btn")
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Super Like", modifier = Modifier.size(24.dp))
                    }

                    // Like Button
                    FloatingActionButton(
                        onClick = { handleLike(currentCardUser) },
                        containerColor = PremPinkPrimary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(68.dp)
                            .testTag("action_like_btn")
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Like", modifier = Modifier.size(36.dp))
                    }
                }
            } else {
                // Empty state when all profiles seen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Empty",
                        tint = PremPinkPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = PremStrings.get("no_more_profiles", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            currentIndex = 0
                            repository.loadDiscoverProfiles()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("refresh_profiles_btn")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(PremStrings.get("refresh", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Full Profile Detail Modal
        if (selectedDetailUser != null) {
            FullProfileDetailDialog(
                user = selectedDetailUser!!,
                language = language,
                onDismiss = { selectedDetailUser = null },
                onLike = {
                    val target = selectedDetailUser!!
                    selectedDetailUser = null
                    handleLike(target)
                },
                onPass = {
                    val target = selectedDetailUser!!
                    selectedDetailUser = null
                    handlePass(target)
                },
                onReport = {
                    reportUserTarget = selectedDetailUser
                    selectedDetailUser = null
                },
                onBlock = {
                    val target = selectedDetailUser!!
                    selectedDetailUser = null
                    repository.blockUser(target.uid)
                }
            )
        }

        // Report Dialog
        if (reportUserTarget != null) {
            ReportDialog(
                reportedUserName = reportUserTarget!!.fullName,
                language = language,
                onDismiss = { reportUserTarget = null },
                onSubmit = { reason, details ->
                    coroutineScope.launch {
                        repository.reportUser(reportUserTarget!!.uid, reportUserTarget!!.fullName, reason, details)
                        reportUserTarget = null
                    }
                }
            )
        }

        // Filter Dialog
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(PremStrings.get("filter", language), fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        // Gender Filter
                        Text("Interested in:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                            listOf("All", "Female", "Male").forEach { g ->
                                val isSelected = (selectedGenderFilter == g)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) PremPinkPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { selectedGenderFilter = g }
                                ) {
                                    Text(
                                        text = g,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Division Filter
                        Text("Division (বিভাগ):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        var isDivExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = isDivExpanded,
                            onExpandedChange = { isDivExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedDivisionFilter,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDivExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            )
                            ExposedDropdownMenu(
                                expanded = isDivExpanded,
                                onDismissRequest = { isDivExpanded = false }
                            ) {
                                (listOf("All") + BangladeshiData.divisions).forEach { d ->
                                    DropdownMenuItem(
                                        text = { Text(d) },
                                        onClick = {
                                            selectedDivisionFilter = d
                                            isDivExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Age Range
                        Text(
                            text = "Age: ${minAgeFilter.toInt()} - ${maxAgeFilter.toInt()} years",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        RangeSlider(
                            value = minAgeFilter..maxAgeFilter,
                            onValueChange = { range ->
                                minAgeFilter = range.start
                                maxAgeFilter = range.endInclusive
                            },
                            valueRange = 18f..55f,
                            steps = 36
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentIndex = 0
                            showFilterDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary)
                    ) {
                        Text("Apply", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedGenderFilter = "All"
                        selectedDivisionFilter = "All"
                        minAgeFilter = 18f
                        maxAgeFilter = 45f
                        currentIndex = 0
                        showFilterDialog = false
                    }) {
                        Text("Reset")
                    }
                }
            )
        }
    }
}
