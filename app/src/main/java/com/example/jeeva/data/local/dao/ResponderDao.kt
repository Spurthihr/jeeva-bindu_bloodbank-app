package com.example.jeeva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jeeva.data.local.entity.RequestResponderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertResponder(responder: RequestResponderEntity)

    @Query("SELECT COUNT(*) FROM request_responders WHERE requestId = :requestId")
    fun getResponderCount(requestId: Int): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM request_responders WHERE requestId = :requestId AND donorPhone = :donorPhone)")
    suspend fun hasResponded(requestId: Int, donorPhone: String): Boolean
}
