package com.example.jeeva.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val phone: String,
    val bloodGroup: String,
    val location: String,
    val isAvailable: Boolean,
    val lastDonationDate: String = "Never",
    val donationCount: Int = 0
)
