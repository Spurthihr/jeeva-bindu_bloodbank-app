package com.example.jeeva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jeeva.data.local.entity.DonationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Query("SELECT * FROM donation_history WHERE donorPhone = :phone ORDER BY timestamp DESC")
    fun getDonationHistory(phone: String): Flow<List<DonationEntity>>
}
