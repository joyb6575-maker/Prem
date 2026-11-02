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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PremRepository
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDashboardScreen(
    repository: PremRepository,
    language: AppLanguage,
    onEditProfile: () -> Unit,
    onOpenSafety: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUserProfile.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // User Profile Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AsyncImage(
                        model = currentUser?.profilePhotoUrl?.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, PremPinkPrimary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${currentUser?.fullName ?: "User"}, ${currentUser?.age ?: 22}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (currentUser?.isVerified == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge(isVerified = true)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${currentUser?.city ?: "Dhaka"}, ${currentUser?.division ?: "Dhaka"}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                if (!currentUser?.profession.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentUser?.profession ?: "",
                        fontSize = 13.sp,
                        color = PremPurpleSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("edit_profile_btn")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(PremStrings.get("edit_profile", language), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Incognito / Hide Profile Toggle
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        if (currentUser?.isHidden == true) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Incognito",
                        tint = PremPurpleSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = PremStrings.get("hide_profile", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = PremStrings.get("hide_profile_desc", language),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                Switch(
                    checked = currentUser?.isHidden == true,
                    onCheckedChange = { isChecked ->
                        coroutineScope.launch { repository.updateIncognito(isChecked) }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = PremPinkPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Options List
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Premium Upgrade
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPremium() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Premium", tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(PremStrings.get("prem_premium", language), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Unlimited likes & profile boost", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Safety Center
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSafety() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Security, contentDescription = "Safety", tint = PremPurpleSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(PremStrings.get("safety_center", language), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Privacy settings & guidelines", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Admin Dashboard (Accessible for demonstration / admin role)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAdmin() }
                        .padding(16.dp)
                        .testTag("admin_panel_entry_btn"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin", tint = Color(0xFF00B0FF))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(PremStrings.get("admin_panel", language), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("User accounts & safety moderation", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Logout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { repository.auth.signOut() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(PremStrings.get("logout", language), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Delete Account
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(PremStrings.get("delete_profile", language), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(PremStrings.get("delete_profile", language)) },
            text = { Text(PremStrings.get("delete_confirm_msg", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.deleteAccount()
                            showDeleteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(PremStrings.get("delete", language), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(PremStrings.get("cancel", language))
                }
            }
        )
    }
}
