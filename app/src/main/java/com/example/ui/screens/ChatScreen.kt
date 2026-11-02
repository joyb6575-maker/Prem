package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PremRepository
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.model.ChatMessage
import com.example.model.UserProfile
import com.example.ui.components.ReportDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    matchId: String,
    otherUser: UserProfile,
    repository: PremRepository,
    language: AppLanguage,
    onBack: () -> Unit,
    onViewProfile: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUid = repository.auth.currentUser?.uid ?: ""

    val messagesFlow = remember(matchId) { repository.observeMessages(matchId) }
    val messages by messagesFlow.collectAsState(initial = emptyList())

    var messageText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleSend() {
        if (messageText.isBlank()) return
        val textToSend = messageText.trim()
        messageText = ""
        coroutineScope.launch {
            repository.sendMessage(matchId, otherUser.uid, textToSend)
        }
    }

    fun handleSendPhoto() {
        // Send a romantic tea date photo / preset image
        coroutineScope.launch {
            repository.sendMessage(
                matchId,
                otherUser.uid,
                text = "Sharing a sweet moment! ☕❤️",
                imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // Chat Header TopAppBar
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onViewProfile(otherUser) }
                ) {
                    Box {
                        AsyncImage(
                            model = otherUser.profilePhotoUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                            contentDescription = otherUser.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        if (otherUser.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = otherUser.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (otherUser.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(isVerified = true)
                            }
                        }
                        Text(
                            text = if (otherUser.isOnline) PremStrings.get("online", language) else PremStrings.get("offline", language),
                            fontSize = 11.sp,
                            color = if (otherUser.isOnline) Color(0xFF00E676) else Color.Gray
                        )
                    }
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.testTag("chat_menu_btn")) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile") },
                            onClick = {
                                showMenu = false
                                onViewProfile(otherUser)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(PremStrings.get("block_user", language), color = Color.Gray) },
                            onClick = {
                                showMenu = false
                                repository.blockUser(otherUser.uid)
                                Toast.makeText(context, PremStrings.get("user_blocked_msg", language), Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(PremStrings.get("report_user", language), color = Color.Red) },
                            onClick = {
                                showMenu = false
                                showReportDialog = true
                            }
                        )
                    }
                }
            }
        )

        // Safety Tip Banner
        Surface(
            color = Color(0xFFFFF3E0),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Filled.Security,
                    contentDescription = "Safety",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = PremStrings.get("safety_tip", language),
                    fontSize = 11.sp,
                    color = Color(0xFFE65100),
                    lineHeight = 14.sp
                )
            }
        }

        // Messages LazyColumn
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = PremStrings.get("no_messages_yet", language),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            items(messages) { msg ->
                val isMe = msg.senderId == currentUid
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val timeStr = timeFormat.format(Date(msg.timestamp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            color = if (isMe) PremPinkPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (msg.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = msg.imageUrl,
                                        contentDescription = "Shared Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                if (msg.text.isNotBlank()) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = timeStr,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }
            }
        }

        // Message Input Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                IconButton(
                    onClick = { handleSendPhoto() },
                    modifier = Modifier.testTag("send_photo_btn")
                ) {
                    Icon(
                        Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Share Photo",
                        tint = PremPurpleSecondary
                    )
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text(PremStrings.get("type_message", language), fontSize = 14.sp) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PremPinkPrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { handleSend() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PremPinkPrimary)
                        .testTag("send_message_btn")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            reportedUserName = otherUser.fullName,
            language = language,
            onDismiss = { showReportDialog = false },
            onSubmit = { reason, details ->
                coroutineScope.launch {
                    repository.reportUser(otherUser.uid, otherUser.fullName, reason, details)
                    showReportDialog = false
                    Toast.makeText(context, PremStrings.get("report_submitted", language), Toast.LENGTH_LONG).show()
                    onBack()
                }
            }
        )
    }
}
