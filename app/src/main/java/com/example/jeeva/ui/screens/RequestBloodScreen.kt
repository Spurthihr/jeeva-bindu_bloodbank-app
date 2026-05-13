package com.example.jeeva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.ui.theme.RedPrimary
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    viewModel: BloodRequestViewModel,
    onBack: () -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var unitsRequired by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf("High") }

    val isSaving by viewModel.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Blood", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSaving) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = patientName,
                onValueChange = { patientName = it },
                label = { Text("Patient Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group Needed") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = hospitalName,
                onValueChange = { hospitalName = it },
                label = { Text("Hospital Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = unitsRequired,
                onValueChange = { unitsRequired = it },
                label = { Text("Units Required") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            Text("Urgency Level", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("High", "Medium", "Low").forEach { level ->
                    FilterChip(
                        selected = urgencyLevel == level,
                        onClick = { urgencyLevel = level },
                        label = { Text(level) },
                        enabled = !isSaving
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (patientName.isNotBlank() && bloodGroup.isNotBlank()) {
                        viewModel.addRequest(
                            BloodRequestEntity(
                                patientName = patientName,
                                bloodGroup = bloodGroup,
                                hospitalName = hospitalName,
                                location = location,
                                unitsRequired = unitsRequired,
                                contactNumber = contactNumber,
                                urgencyLevel = urgencyLevel
                            )
                        ) {
                            onBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Request", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
