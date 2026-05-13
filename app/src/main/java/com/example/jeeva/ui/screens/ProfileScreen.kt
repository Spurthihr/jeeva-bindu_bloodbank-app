package com.example.jeeva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeva.data.local.entity.DonorEntity
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: DonorViewModel) {
    val user = viewModel.currentUser
    val donationHistory by viewModel.donationHistory.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No donor profile found.", fontWeight = FontWeight.Bold)
                    Text("Please register as a donor to see your profile.", color = Color.Gray)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8F9FA))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(RedPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = RedPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp), 
                        tint = Color.Gray
                    )
                    Text(text = " ${user.location} • Age: ${user.age}", color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = RedPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Blood Group: ${user.bloodGroup}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                DonationTrackerSection(
                    lastDonation = user.lastDonationDate,
                    isEligible = viewModel.isEligible(user.lastDonationDate),
                    nextEligibleDate = viewModel.getNextEligibleDate(user.lastDonationDate),
                    remainingDays = viewModel.getRemainingDays(user.lastDonationDate)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                if (viewModel.isEligible(user.lastDonationDate)) {
                    Button(
                        onClick = { viewModel.completeDonation() },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) {
                        Text("I Just Donated Blood")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                ) {
                    ProfileOptionItem(Icons.Default.Edit, "Edit Profile") { showEditDialog = true }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    ProfileOptionItem(Icons.Default.History, "Donation History") { showHistoryDialog = true }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                viewModel.updateProfile(updatedUser) {
                    showEditDialog = false
                }
            }
        )
    }

    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = { Text("Donation History", fontWeight = FontWeight.Bold) },
            text = {
                if (donationHistory.isEmpty()) {
                    Text("No donations recorded yet.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(donationHistory) { donation ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(donation.donationDate)
                                Text("Success", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close", color = RedPrimary)
                }
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    user: DonorEntity,
    onDismiss: () -> Unit,
    onSave: (DonorEntity) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var age by remember { mutableStateOf(user.age.toString()) }
    var location by remember { mutableStateOf(user.location) }
    var bloodGroup by remember { mutableStateOf(user.bloodGroup) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(user.copy(
                        name = name,
                        age = age.toIntOrNull() ?: user.age,
                        location = location,
                        bloodGroup = bloodGroup
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun DonationTrackerSection(
    lastDonation: String?,
    isEligible: Boolean,
    nextEligibleDate: String,
    remainingDays: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Donation Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Surface(
                    color = if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = CircleShape
                ) {
                    Text(
                        if (isEligible) "Eligible to Donate" else "Recovery Phase",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (isEligible) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
            
            if (!isEligible) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = (90f - remainingDays.coerceIn(0, 90).toFloat()) / 90f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = RedPrimary,
                    trackColor = Color(0xFFFFEBEE)
                )
                Text(
                    text = "$remainingDays days remaining until next donation",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Last Donation", color = Color.Gray, fontSize = 12.sp)
                    Text(lastDonation ?: "Never", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Next Eligible", color = Color.Gray, fontSize = 12.sp)
                    Text(nextEligibleDate, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    label: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (textColor == RedPrimary) RedPrimary else Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = textColor, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}
