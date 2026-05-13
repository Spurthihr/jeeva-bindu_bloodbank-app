package com.example.jeeva.data.repository

import com.example.jeeva.data.local.dao.BloodRequestDao
import com.example.jeeva.data.local.dao.ResponderDao
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.data.local.entity.RequestResponderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BloodRequestRepository(
    private val bloodRequestDao: BloodRequestDao,
    private val responderDao: ResponderDao
) {
    val allRequests: Flow<List<BloodRequestEntity>> = bloodRequestDao.getAllRequests()

    suspend fun insert(request: BloodRequestEntity) {
        withContext(Dispatchers.IO) {
            bloodRequestDao.insertRequest(request)
        }
    }

    suspend fun delete(requestId: Int) {
        withContext(Dispatchers.IO) {
            bloodRequestDao.deleteRequest(requestId)
        }
    }

    fun getResponderCount(requestId: Int): Flow<Int> = responderDao.getResponderCount(requestId)

    suspend fun respondToRequest(requestId: Int, donorPhone: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (responderDao.hasResponded(requestId, donorPhone)) {
                false
            } else {
                responderDao.insertResponder(
                    RequestResponderEntity(requestId = requestId, donorPhone = donorPhone)
                )
                true
            }
        }
    }
}
