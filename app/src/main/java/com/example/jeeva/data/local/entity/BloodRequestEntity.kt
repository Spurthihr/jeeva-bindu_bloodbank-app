package com.example.jeeva.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_requests")
data class BloodRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientName: String,
    val bloodGroup: String,
    val hospitalName: String,
    val location: String,
    val unitsRequired: String,
    val contactNumber: String,
    val urgencyLevel: String, // High, Medium, Low
    val time: Long = System.currentTimeMillis()
)
