package com.example.jeeva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.DonorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: DonorViewModel
) {
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "🩸",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (!isOtpSent) "Welcome to Jeeva" else "Verify OTP",
                fontSize = 28.sp,
                color = RedPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (!isOtpSent) 
                    "Enter your phone number to continue" 
                else 
                    "Enter the code sent to +91 $phone",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedContent(
                targetState = isOtpSent,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it } togetherWith
                            fadeOut() + slideOutHorizontally { -it }
                }, label = ""
            ) { otpSent ->
                if (!otpSent) {
                    PhoneInputSection(
                        phone = phone,
                        onPhoneChange = { if (it.length <= 10) phone = it },
                        isLoading = isLoading,
                        onSendOtp = {
                            if (phone.length == 10) {
                                scope.launch {
                                    isLoading = true
                                    delay(1500) // Simulate network call
                                    isLoading = false
                                    isOtpSent = true
                                    snackbarHostState.showSnackbar("OTP sent successfully! (Try 1234)")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid 10-digit number")
                                }
                            }
                        }
                    )
                } else {
                    OtpInputSection(
                        otp = otp,
                        onOtpChange = { if (it.length <= 6) otp = it },
                        isLoading = isLoading,
                        onVerify = {
                            if (otp == "1234") {
                                scope.launch {
                                    isLoading = true
                                    delay(1500) // Simulate verification
                                    isLoading = false
                                    viewModel.login(phone)
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            } else {
                                scope.launch {
                                    if (otp.length < 4) {
                                        snackbarHostState.showSnackbar("Please enter a valid OTP")
                                    } else {
                                        snackbarHostState.showSnackbar("Invalid OTP. Try 1234")
                                    }
                                }
                            }
                        },
                        onBack = { isOtpSent = false }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneInputSection(
    phone: String,
    onPhoneChange: (String) -> Unit,
    isLoading: Boolean,
    onSendOtp: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            prefix = { Text("+91 ") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSendOtp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("SEND OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun OtpInputSection(
    otp: String,
    onOtpChange: (String) -> Unit,
    isLoading: Boolean,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = otp,
            onValueChange = onOtpChange,
            label = { Text("OTP Code") },
            placeholder = { Text("Enter 4-digit code") },
            leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("VERIFY & LOGIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
            enabled = !isLoading
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Change Phone Number", color = Color.Gray)
        }
    }
}
