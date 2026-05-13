package com.example.jeeva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jeeva.ui.components.DonorCard
import com.example.jeeva.ui.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorsScreen(viewModel: DonorViewModel) {
    val donors by viewModel.filteredDonors.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Available Donors", fontWeight = FontWeight.Bold) },
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
            FilterSection(viewModel)
            
            if (donors.isEmpty()) {
                EmptyStateUI("No donors found.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(donors) { donor ->
                        DonorCard(
                            name = donor.name,
                            bloodGroup = donor.bloodGroup,
                            location = donor.location,
                            phone = donor.phone,
                            isAvailable = donor.isAvailable,
                            isEligible = viewModel.isEligible(donor.lastDonationDate)
                        )
                    }
                }
            }
        }
    }
}
