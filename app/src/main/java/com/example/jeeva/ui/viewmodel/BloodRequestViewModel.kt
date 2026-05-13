package com.example.jeeva.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeva.data.local.JeevaDatabase
import com.example.jeeva.data.local.entity.BloodRequestEntity
import com.example.jeeva.data.repository.BloodRequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BloodRequestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BloodRequestRepository
    val allRequests: StateFlow<List<BloodRequestEntity>>

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        val database = JeevaDatabase.getDatabase(application)
        repository = BloodRequestRepository(database.bloodRequestDao(), database.responderDao())
        allRequests = repository.allRequests
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addRequest(request: BloodRequestEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                repository.insert(request)
                Toast.makeText(getApplication(), "Emergency request posted successfully!", Toast.LENGTH_SHORT).show()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), "Failed to post request: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteRequest(requestId: Int) {
        viewModelScope.launch {
            repository.delete(requestId)
        }
    }

    fun getResponderCount(requestId: Int): Flow<Int> = repository.getResponderCount(requestId)

    fun respondToRequest(requestId: Int, donorPhone: String) {
        viewModelScope.launch {
            val success = repository.respondToRequest(requestId, donorPhone)
            if (!success) {
                Toast.makeText(getApplication(), "You already responded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), "Response sent! Thank you.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
