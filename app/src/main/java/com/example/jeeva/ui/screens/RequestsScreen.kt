package com.example.jeeva.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeva.data.local.SessionManager
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(viewModel: BloodRequestViewModel) {
    val requests by viewModel.allRequests.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val donorPhone = sessionManager.getUserPhone() ?: ""

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Blood Requests", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            if (requests.isEmpty()) {
                EmptyStateUI("No active blood requests.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests) { request ->
                        val responderCount by viewModel.getResponderCount(request.id).collectAsState(initial = 0)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = RedPrimary,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    ) {
                                        Text(
                                            request.bloodGroup,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    val urgencyColor = when(request.urgencyLevel) {
                                        "High" -> RedPrimary
                                        "Medium" -> Color(0xFFFFA000)
                                        else -> Color(0xFF4CAF50)
                                    }
                                    
                                    Text(
                                        request.urgencyLevel,
                                        color = urgencyColor,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Patient: ${request.patientName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Hospital: ${request.hospitalName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Location: ${request.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Units: ${request.unitsRequired}", fontWeight = FontWeight.Medium)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.People, 
                                                contentDescription = null, 
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "$responderCount people coming", 
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Text(
                                        "Contact: ${request.contactNumber}",
                                        color = RedPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${request.contactNumber}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            val message = "Hello, I saw your blood request for ${request.patientName} (${request.bloodGroup}) on Jeeva app. I would like to help."
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                data = Uri.parse("https://api.whatsapp.com/send?phone=${request.contactNumber}&text=${Uri.encode(message)}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { 
                                        if (donorPhone.isNotEmpty()) {
                                            viewModel.respondToRequest(request.id, donorPhone)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("I'm Coming", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
