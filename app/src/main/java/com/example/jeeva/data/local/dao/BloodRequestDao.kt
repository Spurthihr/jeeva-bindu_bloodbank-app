package com.example.jeeva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jeeva.data.local.entity.BloodRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequestEntity)

    @Query("SELECT * FROM blood_requests ORDER BY time DESC")
    fun getAllRequests(): Flow<List<BloodRequestEntity>>

    @Query("DELETE FROM blood_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: Int)
}
