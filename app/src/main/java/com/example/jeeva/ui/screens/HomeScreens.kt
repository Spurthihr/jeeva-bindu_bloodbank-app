package com.example.jeeva.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeva.data.local.SessionManager
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.ui.components.DonorCard
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel
import com.example.jeeva.ui.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    donorViewModel: DonorViewModel,
    requestViewModel: BloodRequestViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToRequest: () -> Unit,
    onNavigateToAllDonors: () -> Unit,
    onNavigateToAllRequests: () -> Unit
) {
    val donors by donorViewModel.filteredDonors.collectAsState()
    val availableDonorCount by donorViewModel.availableDonorCount.collectAsState()
    val requests by requestViewModel.allRequests.collectAsState()
    val searchQuery = donorViewModel.searchQuery
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val donorPhone = sessionManager.getUserPhone() ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🩸 ", fontSize = 24.sp)
                        Text(
                            "Jeeva-Bindu",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedPrimary
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDFDFD))
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { donorViewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search by name, group, or location") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }

            item {
                QuickActionsSection(onNavigateToRegister, onNavigateToRequest)
            }

            item {
                SectionHeader(title = "Impact Statistics", actionText = "")
                StatisticsSection(donorCount = availableDonorCount, requestCount = requests.size)
            }

            if (requests.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Emergency Requests",
                        actionText = "View All",
                        onActionClick = onNavigateToAllRequests
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(requests.take(5)) { request ->
                            EmergencyRequestCard(
                                request = request,
                                viewModel = requestViewModel,
                                donorPhone = donorPhone
                            )
                        }
                    }
                }
            }

            item {
                FilterSection(donorViewModel)
            }

            item {
                SectionHeader(
                    title = "Recent Donors",
                    actionText = "View All",
                    onActionClick = onNavigateToAllDonors
                )
            }

            if (donors.isEmpty()) {
                item {
                    EmptyStateUI("No donors found matching criteria.")
                }
            } else {
                items(donors.take(5)) { donor ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DonorCard(
                            name = donor.name,
                            bloodGroup = donor.bloodGroup,
                            location = donor.location,
                            phone = donor.phone,
                            isAvailable = donor.isAvailable,
                            isEligible = donorViewModel.isEligible(donor.lastDonationDate)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun EmergencyRequestCard(
    request: BloodRequestEntity,
    viewModel: BloodRequestViewModel,
    donorPhone: String
) {
    val context = LocalContext.current
    val responderCount by viewModel.getResponderCount(request.id).collectAsState(initial = 0)

    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RedPrimary.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, RedPrimary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = RedPrimary,
                    shape = CircleShape
                ) {
                    Text(
                        request.bloodGroup,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Urgent: ${request.unitsRequired} Units",
                    style = MaterialTheme.typography.labelLarge,
                    color = RedPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(request.hospitalName, fontWeight = FontWeight.Bold)
            Text(request.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.People, 
                    contentDescription = null, 
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "$responderCount people coming", 
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Contact: ${request.contactNumber}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { 
                    if (donorPhone.isNotEmpty()) {
                        viewModel.respondToRequest(request.id, donorPhone)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("I'm Coming", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: DonorViewModel) {
    val selectedGroup = viewModel.selectedBloodGroup
    val availableOnly = viewModel.availableOnly
    val bloodGroups = remember { listOf("All", "O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-") }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bloodGroups) { group ->
                FilterChip(
                    selected = selectedGroup == group,
                    onClick = { viewModel.onBloodGroupFilterChange(group) },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = availableOnly,
                onCheckedChange = { viewModel.toggleAvailableOnly(it) },
                colors = CheckboxDefaults.colors(checkedColor = RedPrimary)
            )
            Text("Available Only", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (actionText.isNotEmpty()) {
            TextButton(onClick = onActionClick) {
                Text(actionText, color = RedPrimary)
            }
        }
    }
}

@Composable
fun EmptyStateUI(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun QuickActionsSection(onNavigateToRegister: () -> Unit, onNavigateToRequest: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(
            title = "Register Donor",
            icon = Icons.Default.AppRegistration,
            containerColor = Color(0xFFFFEBEE),
            iconColor = RedPrimary,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToRegister
        )
        QuickActionButton(
            title = "Request Blood",
            icon = Icons.Default.Opacity,
            containerColor = Color(0xFFFFEBEE),
            iconColor = RedPrimary,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToRequest
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun StatisticsSection(donorCount: Int, requestCount: Int) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            StatCard("Available Donors", "$donorCount", Icons.Default.Group, Color(0xFF4CAF50))
        }
        item {
            StatCard("Emergency Requests", "$requestCount", Icons.Default.NotificationImportant, RedPrimary)
        }
        item {
            StatCard("Blood Groups", "8", Icons.AutoMirrored.Filled.List, Color(0xFF2196F3))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
