package com.example.jeeva.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel
import com.example.jeeva.ui.viewmodel.DonorViewModel

@Composable
fun EmergencyRequestCard(
    request: BloodRequestEntity,
    requestViewModel: BloodRequestViewModel,
    donorViewModel: DonorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser = donorViewModel.currentUser
    val responderCount by requestViewModel.getResponderCount(request.id).collectAsState(initial = 0)
    
    val isEligible = donorViewModel.isEligible(currentUser?.lastDonationDate)
    val isBloodGroupMatch = currentUser?.bloodGroup == request.bloodGroup
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, RedPrimary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RedPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.bloodGroup,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.People, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$responderCount Responders",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Urgent: ${request.unitsRequired} Units",
                style = MaterialTheme.typography.titleMedium,
                color = RedPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = request.hospitalName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            
            Text(
                text = request.location,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${request.contactNumber}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.DarkGray)
                }

                IconButton(
                    onClick = {
                        val message = "Emergency Alert! I saw your blood request for ${request.bloodGroup} in ${request.location} on Jeeva app. I'm available to help!"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=${request.contactNumber}&text=${Uri.encode(message)}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF25D366).copy(alpha = 0.1f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            if (currentUser != null) {
                if (!isEligible) {
                    StatusMessage("Not Eligible (Recovery Phase)")
                } else if (!isBloodGroupMatch) {
                    StatusMessage("Blood Group Mismatch")
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            requestViewModel.respondToRequest(request.id, currentUser.phone)
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = isEligible && isBloodGroupMatch,
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("I'M COMING", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            donorViewModel.completeDonation()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = isEligible && isBloodGroupMatch,
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DONE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            } else {
                Text(
                    text = "Login as donor to respond",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun StatusMessage(message: String) {
    Surface(
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFE65100),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
