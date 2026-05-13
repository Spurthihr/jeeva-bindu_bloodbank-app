package com.example.jeeva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jeeva.data.local.entity.DonorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: DonorEntity)

    @Query("SELECT * FROM donors ORDER BY id DESC")
    fun getAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT COUNT(*) FROM donors")
    fun getDonorCount(): Flow<Int>

    @Query("""
        SELECT * FROM donors 
        WHERE (:group = 'All' OR bloodGroup = :group) 
        AND (name LIKE '%' || :searchQuery || '%' OR location LIKE '%' || :searchQuery || '%')
        AND (:availableOnly = 0 OR isAvailable = :availableOnly)
        ORDER BY id DESC
    """)
    fun searchDonors(searchQuery: String, group: String, availableOnly: Boolean): Flow<List<DonorEntity>>
}
