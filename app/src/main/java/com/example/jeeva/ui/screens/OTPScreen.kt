package com.example.jeeva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.DonorViewModel
import kotlinx.coroutines.delay

@Composable
fun OTPScreen(
    viewModel: DonorViewModel,
    phone: String,
    onNavigateToHome: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jeeva-Bindu Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RedPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter the 4-digit code sent to +91 $phone",
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { 
                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    otp = it
                    errorMessage = null
                }
            },
            label = { Text("Enter OTP (Try 1234)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                letterSpacing = 8.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (otp == "1234") {
                    isVerifying = true
                } else {
                    errorMessage = "Invalid OTP. Use 1234"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !isVerifying && otp.length == 4
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("VERIFY & CONTINUE", fontWeight = FontWeight.Bold)
            }
        }

        LaunchedEffect(isVerifying) {
            if (isVerifying) {
                delay(1500) // Simulated loading
                viewModel.login(phone)
                onNavigateToHome()
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { /* Simulated resend */ },
            enabled = !isVerifying
        ) {
            Text("Resend Code", color = RedPrimary)
        }
    }
}
