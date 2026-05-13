package com.example.jeeva.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jeeva.data.local.dao.BloodRequestDao
import com.example.jeeva.data.local.dao.DonationDao
import com.example.jeeva.data.local.dao.DonorDao
import com.example.jeeva.data.local.dao.ResponderDao
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.data.local.entity.DonationEntity
import com.example.jeeva.data.local.entity.DonorEntity
import com.example.jeeva.data.local.entity.RequestResponderEntity

@Database(
    entities = [DonorEntity::class, BloodRequestEntity::class, RequestResponderEntity::class, DonationEntity::class],
    version = 4,
    exportSchema = false
)
abstract class JeevaDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun bloodRequestDao(): BloodRequestDao
    abstract fun responderDao(): ResponderDao
    abstract fun donationDao(): DonationDao

    companion object {
        @Volatile
        private var INSTANCE: JeevaDatabase? = null

        fun getDatabase(context: Context): JeevaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JeevaDatabase::class.java,
                    "jeeva_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
