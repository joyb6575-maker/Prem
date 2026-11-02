package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.PremStrings
import com.example.model.AppLanguage
import com.example.ui.theme.PremPinkPrimary
import com.example.ui.theme.PremPurpleSecondary
import com.example.ui.theme.PremRoseTertiary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun WelcomeAuthScreen(
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onAuthSuccess: (userId: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Google & Phone, 1: Email

    var isAgeConfirmed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Phone / OTP states
    var phoneNumber by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }

    // Email states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    fun handleGoogleSignIn() {
        if (!isAgeConfirmed) {
            errorMessage = PremStrings.get("must_be_18", language)
            return
        }
        isLoading = true
        errorMessage = null

        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val serverClientId = context.getString(R.string.default_web_client_id)
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context as Activity
                )

                val credential = result.credential
                if (credential is androidx.credentials.CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        onAuthSuccess(user.uid)
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                // User cancelled the prompt
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Google Sign-In failed"
            }
        }
    }

    fun handlePhoneSubmit() {
        if (!isAgeConfirmed) {
            errorMessage = PremStrings.get("must_be_18", language)
            return
        }
        if (phoneNumber.length < 11) {
            errorMessage = "Please enter a valid 11-digit Bangladeshi mobile number (e.g. 01712345678)"
            return
        }
        errorMessage = null
        showOtpDialog = true
    }

    fun handleVerifyOtp() {
        if (enteredOtp == "1234" || enteredOtp.length == 4) {
            isLoading = true
            showOtpDialog = false
            coroutineScope.launch {
                try {
                    // Sign in or create session via phone email proxy for auth
                    val fakeEmail = "phone_${phoneNumber.filter { it.isDigit() }}@prem.bd"
                    val fakePass = "PremPass@123"
                    val auth = Firebase.auth
                    try {
                        val res = auth.signInWithEmailAndPassword(fakeEmail, fakePass).await()
                        onAuthSuccess(res.user!!.uid)
                    } catch (e: Exception) {
                        val res = auth.createUserWithEmailAndPassword(fakeEmail, fakePass).await()
                        onAuthSuccess(res.user!!.uid)
                    }
                } catch (e: Exception) {
                    isLoading = false
                    errorMessage = e.localizedMessage
                }
            }
        } else {
            Toast.makeText(context, "Invalid OTP. Use demo OTP: 1234", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleEmailAuth() {
        if (!isAgeConfirmed) {
            errorMessage = PremStrings.get("must_be_18", language)
            return
        }
        if (email.isBlank() || password.length < 6) {
            errorMessage = "Please provide valid email and password (min 6 characters)"
            return
        }
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val auth = Firebase.auth
                if (isRegisterMode) {
                    val res = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                    onAuthSuccess(res.user!!.uid)
                } else {
                    val res = auth.signInWithEmailAndPassword(email.trim(), password).await()
                    onAuthSuccess(res.user!!.uid)
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Authentication failed"
            }
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
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Banner Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.prem_hero_banner),
                    contentDescription = "Romantic Couple Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Language Toggle in top corner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clickable { onLanguageToggle() }
                        .testTag("welcome_lang_toggle")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Translate,
                            contentDescription = "Language",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BN) "English" else "বাংলা",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                            )
                        )
                )
            }

            // Welcome Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo & App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PremPinkPrimary, PremPurpleSecondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "PREM (প্রেম)",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PremPinkPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tagline & Subtitle
                Text(
                    text = PremStrings.get("tagline", language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = PremStrings.get("subtitle", language),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PremPurpleSecondary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = PremStrings.get("dating_not_marriage", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PremPurpleSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 18+ Mandatory Age Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isAgeConfirmed) PremPinkPrimary.copy(alpha = 0.08f) else Color.Red.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                        .clickable { isAgeConfirmed = !isAgeConfirmed }
                        .testTag("age_18_checkbox_row")
                ) {
                    Checkbox(
                        checked = isAgeConfirmed,
                        onCheckedChange = { isAgeConfirmed = it },
                        modifier = Modifier.testTag("age_18_checkbox")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = PremStrings.get("age_warning", language) + " (18+)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isAgeConfirmed) MaterialTheme.colorScheme.onBackground else Color.Red
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Mode Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = PremPinkPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Google & Mobile", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Email Login", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = PremPinkPrimary, modifier = Modifier.padding(16.dp))
                } else if (selectedTab == 0) {
                    // Google Sign-In Button
                    Button(
                        onClick = { handleGoogleSignIn() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_btn")
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Google",
                            tint = PremPinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = PremStrings.get("login_with_google", language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.LightGray))
                        Text(" OR ", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.LightGray))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 11) phoneNumber = it },
                        label = { Text(PremStrings.get("enter_phone", language)) },
                        placeholder = { Text("01712345678") },
                        leadingIcon = {
                            Icon(Icons.Filled.Phone, contentDescription = "Phone", tint = PremPinkPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { handlePhoneSubmit() },
                        colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("phone_submit_btn")
                    ) {
                        Text(
                            text = PremStrings.get("phone_login", language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // Email & Password Mode
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(PremStrings.get("enter_email", language)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = "Email", tint = PremPurpleSecondary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(PremStrings.get("enter_password", language)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = "Password", tint = PremPurpleSecondary)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { handleEmailAuth() },
                        colors = ButtonDefaults.buttonColors(containerColor = PremPurpleSecondary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("email_submit_btn")
                    ) {
                        Text(
                            text = if (isRegisterMode) PremStrings.get("create_account", language) else PremStrings.get("login", language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(
                            text = if (isRegisterMode) "Already have an account? Login" else "Don't have an account? Create one",
                            color = PremPurpleSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = PremStrings.get("phone_privacy_note", language),
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // OTP Verification Modal
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Text(
                    text = PremStrings.get("verify_otp", language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${PremStrings.get("otp_sent_to", language)} $phoneNumber",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = PremStrings.get("demo_otp_hint", language),
                        color = PremPinkPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { if (it.length <= 4) enteredOtp = it },
                        label = { Text(PremStrings.get("enter_otp", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleVerifyOtp() },
                    colors = ButtonDefaults.buttonColors(containerColor = PremPinkPrimary),
                    modifier = Modifier.testTag("otp_verify_btn")
                ) {
                    Text(PremStrings.get("confirm", language), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text(PremStrings.get("cancel", language))
                }
            }
        )
    }
}
