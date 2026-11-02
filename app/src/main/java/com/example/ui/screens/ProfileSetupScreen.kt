package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BangladeshiData
import com.example.data.PremRepository
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.components.PremTopAppBar
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    repository: PremRepository,
    initialProfile: UserProfile?,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onProfileSaved: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(initialProfile?.fullName ?: "") }
    var ageText by remember { mutableStateOf((initialProfile?.age ?: 22).toString()) }
    var gender by remember { mutableStateOf(initialProfile?.gender ?: "Female") }
    var division by remember { mutableStateOf(initialProfile?.division ?: "Dhaka") }
    var district by remember { mutableStateOf(initialProfile?.district ?: "Dhaka City") }
    var city by remember { mutableStateOf(initialProfile?.city ?: "Dhanmondi") }
    var education by remember { mutableStateOf(initialProfile?.education ?: "") }
    var profession by remember { mutableStateOf(initialProfile?.profession ?: "") }
    var relationshipStatus by remember { mutableStateOf(initialProfile?.relationshipStatus ?: "Single") }
    var bio by remember { mutableStateOf(initialProfile?.bio ?: "") }
    var partnerPreferences by remember { mutableStateOf(initialProfile?.partnerPreferences ?: "") }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&q=80",
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&q=80"
    )

    var profilePhotoUrl by remember {
        mutableStateOf(initialProfile?.profilePhotoUrl?.ifEmpty { presetAvatars[0] } ?: presetAvatars[0])
    }

    val selectedInterests = remember {
        mutableStateListOf<String>().apply {
            addAll(initialProfile?.interests ?: listOf("☕ Adda & Cha", "📚 Bangla Literature"))
        }
    }

    var isDivisionMenuExpanded by remember { mutableStateOf(false) }
    var isDistrictMenuExpanded by remember { mutableStateOf(false) }
    var isGenderMenuExpanded by remember { mutableStateOf(false) }
    var isStatusMenuExpanded by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleSave() {
        val age = ageText.toIntOrNull() ?: 0
        if (fullName.isBlank()) {
            errorMessage = "Please enter your full name"
            return
        }
        if (age < 18) {
            errorMessage = PremStrings.get("must_be_18", language)
            return
        }
        if (profession.isBlank()) {
            errorMessage = "Please state your profession or occupation"
            return
        }
        if (bio.isBlank()) {
            errorMessage = "Please write a few words about yourself"
            return
        }

        isSaving = true
        errorMessage = null

        coroutineScope.launch {
            val userToSave = (initialProfile ?: UserProfile()).copy(
                fullName = fullName.trim(),
                age = age,
                gender = gender,
                division = division,
                district = district,
                city = city.trim(),
                education = education.trim(),
                profession = profession.trim(),
                relationshipStatus = relationshipStatus,
                interests = selectedInterests.toList(),
                bio = bio.trim(),
                partnerPreferences = partnerPreferences.trim(),
                profilePhotoUrl = profilePhotoUrl,
                photos = listOf(profilePhotoUrl)
            )

            val result = repository.saveUserProfile(userToSave)
            isSaving = false
            if (result.isSuccess) {
                Toast.makeText(context, PremStrings.get("success", language), Toast.LENGTH_SHORT).show()
                onProfileSaved()
            } else {
                errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to save profile"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PremTopAppBar(
            title = PremStrings.get("complete_profile", language),
            language = language,
            onLanguageToggle = onLanguageToggle
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Profile Photo Selector
            Text(
                text = PremStrings.get("profile_photo", language),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = profilePhotoUrl,
                    contentDescription = "Selected Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(3.dp, PremPinkPrimary, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Choose an avatar or photo:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetAvatars) { avatarUrl ->
                            val isSelected = (avatarUrl == profilePhotoUrl)
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar Choice",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) PremPinkPrimary else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { profilePhotoUrl = avatarUrl }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name & Age
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(PremStrings.get("full_name", language) + " *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { if (it.length <= 2) ageText = it },
                    label = { Text(PremStrings.get("age", language) + " (18+) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("setup_age_input")
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = isGenderMenuExpanded,
                    onExpandedChange = { isGenderMenuExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(PremStrings.get("gender", language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .testTag("setup_gender_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = isGenderMenuExpanded,
                        onDismissRequest = { isGenderMenuExpanded = false }
                    ) {
                        listOf("Female", "Male", "Other").forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    gender = g
                                    isGenderMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location: Division & District
            Text(
                text = "Location (বাংলাদেশ)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Division
                ExposedDropdownMenuBox(
                    expanded = isDivisionMenuExpanded,
                    onExpandedChange = { isDivisionMenuExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = division,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(PremStrings.get("division", language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDivisionMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = isDivisionMenuExpanded,
                        onDismissRequest = { isDivisionMenuExpanded = false }
                    ) {
                        BangladeshiData.divisions.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    division = d
                                    district = BangladeshiData.divisionDistricts[d]?.firstOrNull() ?: d
                                    isDivisionMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // District
                ExposedDropdownMenuBox(
                    expanded = isDistrictMenuExpanded,
                    onExpandedChange = { isDistrictMenuExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    val districts = BangladeshiData.divisionDistricts[division] ?: listOf("City")
                    OutlinedTextField(
                        value = district,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(PremStrings.get("district", language)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDistrictMenuExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = isDistrictMenuExpanded,
                        onDismissRequest = { isDistrictMenuExpanded = false }
                    ) {
                        districts.forEach { dist ->
                            DropdownMenuItem(
                                text = { Text(dist) },
                                onClick = {
                                    district = dist
                                    isDistrictMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(PremStrings.get("city", language)) },
                placeholder = { Text("e.g. Dhanmondi / Gulshan / Uttara / Panchlaish") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profession & Education
            OutlinedTextField(
                value = profession,
                onValueChange = { profession = it },
                label = { Text(PremStrings.get("profession", language) + " *") },
                placeholder = { Text("e.g. Software Engineer / Doctor / Architect / Student") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = education,
                onValueChange = { education = it },
                label = { Text(PremStrings.get("education", language)) },
                placeholder = { Text("e.g. BSc in CSE - BUET / MBA - IBA, DU") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Relationship Status
            ExposedDropdownMenuBox(
                expanded = isStatusMenuExpanded,
                onExpandedChange = { isStatusMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = relationshipStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(PremStrings.get("relationship_status", language)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusMenuExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = isStatusMenuExpanded,
                    onDismissRequest = { isStatusMenuExpanded = false }
                ) {
                    listOf("Single", "Never Married", "Divorced", "Separated").forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st) },
                            onClick = {
                                relationshipStatus = st
                                isStatusMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interests Chips (Multi-select)
            Text(
                text = PremStrings.get("interests", language),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BangladeshiData.popularInterests.forEach { interest ->
                    val isSelected = interest in selectedInterests
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) PremPinkPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            if (isSelected) selectedInterests.remove(interest) else selectedInterests.add(interest)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = interest,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(PremStrings.get("bio", language) + " *") },
                placeholder = { Text("Write something charming about yourself, what you love doing in your free time...") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_bio_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Partner Preferences
            OutlinedTextField(
                value = partnerPreferences,
                onValueChange = { partnerPreferences = it },
                label = { Text(PremStrings.get("partner_pref", language)) },
                placeholder = { Text("Describe the qualities you appreciate in a partner or companion...") },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isSaving) {
                CircularProgressIndicator(color = PremPinkPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { handleSave() },
                    colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_profile_btn")
                ) {
                    Text(
                        text = PremStrings.get("save_profile", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
