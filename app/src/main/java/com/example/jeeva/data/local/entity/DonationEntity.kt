package com.example.jeeva.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_history")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorPhone: String,
    val donationDate: String,
    val timestamp: Long = System.currentTimeMillis()
)
