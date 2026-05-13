package com.example.jeeva.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_responders")
data class RequestResponderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val requestId: Int,
    val donorPhone: String,
    val timestamp: Long = System.currentTimeMillis()
)
