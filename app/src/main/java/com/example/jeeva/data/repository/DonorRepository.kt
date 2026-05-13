package com.example.jeeva.data.repository

import com.example.jeeva.data.local.dao.DonationDao
import com.example.jeeva.data.local.dao.DonorDao
import com.example.jeeva.data.local.entity.DonationEntity
import com.example.jeeva.data.local.entity.DonorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DonorRepository(
    private val donorDao: DonorDao,
    private val donationDao: DonationDao
) {
    val allDonors: Flow<List<DonorEntity>> = donorDao.getAllDonors()
    
    fun getAvailableDonorCount(): Flow<Int> = donorDao.getAvailableDonorCount()
    
    fun searchDonors(query: String, group: String, availableOnly: Boolean): Flow<List<DonorEntity>> {
        return donorDao.searchDonors(query, group, availableOnly)
    }

    suspend fun insert(donor: DonorEntity) {
        withContext(Dispatchers.IO) {
            donorDao.insertDonor(donor)
        }
    }

    fun getDonationHistory(phone: String): Flow<List<DonationEntity>> {
        return donationDao.getDonationHistory(phone)
    }

    suspend fun insertDonation(donation: DonationEntity) {
        withContext(Dispatchers.IO) {
            donationDao.insertDonation(donation)
        }
    }
}
