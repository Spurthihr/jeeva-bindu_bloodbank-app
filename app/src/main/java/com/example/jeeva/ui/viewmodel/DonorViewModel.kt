package com.example.jeeva.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeva.data.local.JeevaDatabase
import com.example.jeeva.data.local.SessionManager
import com.example.jeeva.data.local.entity.DonationEntity
import com.example.jeeva.data.local.entity.DonorEntity
import com.example.jeeva.data.repository.DonorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DonorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DonorRepository
    private val sessionManager = SessionManager(application)
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val donors: StateFlow<List<DonorEntity>>
    val availableDonorCount: StateFlow<Int>

    var currentUser by mutableStateOf<DonorEntity?>(null)
        private set

    var searchQuery by mutableStateOf("")
    var selectedBloodGroup by mutableStateOf("All")
    var availableOnly by mutableStateOf(false)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredDonors: StateFlow<List<DonorEntity>>

    val donationHistory: StateFlow<List<DonationEntity>> = 
        snapshotFlow { currentUser?.phone }
            .filterNotNull()
            .flatMapLatest { phone -> repository.getDonationHistory(phone) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val database = JeevaDatabase.getDatabase(application)
        repository = DonorRepository(database.donorDao(), database.donationDao())
        
        donors = repository.allDonors
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        availableDonorCount = repository.getAvailableDonorCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

        @OptIn(ExperimentalCoroutinesApi::class)
        filteredDonors = combine(
            snapshotFlow { searchQuery },
            snapshotFlow { selectedBloodGroup },
            snapshotFlow { availableOnly }
        ) { query, group, available ->
            repository.searchDonors(query, group, available)
        }.flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        if (sessionManager.isLoggedIn()) {
            val phone = sessionManager.getUserPhone()
            viewModelScope.launch {
                donors.collectLatest { list ->
                    currentUser = list.find { it.phone == phone }
                }
            }
        }
    }

    fun registerDonor(donor: DonorEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                repository.insert(donor)
                Toast.makeText(getApplication(), "Donor registered successfully!", Toast.LENGTH_SHORT).show()
                if (currentUser == null && donor.phone == sessionManager.getUserPhone()) {
                    currentUser = donor
                }
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateProfile(donor: DonorEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                repository.insert(donor)
                currentUser = donor
                Toast.makeText(getApplication(), "Profile updated!", Toast.LENGTH_SHORT).show()
                onComplete()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun completeDonation() {
        val user = currentUser ?: return
        val today = dateFormatter.format(Date())
        val updatedUser = user.copy(
            lastDonationDate = today,
            donationCount = user.donationCount + 1,
            isAvailable = false
        )
        viewModelScope.launch {
            repository.insert(updatedUser)
            repository.insertDonation(DonationEntity(donorPhone = user.phone, donationDate = today))
            currentUser = updatedUser
        }
    }

    fun login(phone: String) {
        sessionManager.setLogin(true, phone)
        currentUser = donors.value.find { it.phone == phone }
    }

    fun logout(onComplete: () -> Unit) {
        sessionManager.logout()
        currentUser = null
        onComplete()
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun onSearchQueryChange(newQuery: String) { searchQuery = newQuery }
    fun onBloodGroupFilterChange(newGroup: String) { selectedBloodGroup = newGroup }
    fun toggleAvailableOnly(available: Boolean) { availableOnly = available }

    // Eligibility Logic
    fun isEligible(lastDonationDate: String?): Boolean {
        if (lastDonationDate == null || lastDonationDate == "Never" || lastDonationDate.isBlank()) return true
        return try {
            val lastDate = dateFormatter.parse(lastDonationDate) ?: return true
            val calendar = Calendar.getInstance()
            calendar.time = lastDate
            calendar.add(Calendar.DAY_OF_YEAR, 90)
            Date().after(calendar.time)
        } catch (e: Exception) {
            true
        }
    }

    fun getNextEligibleDate(lastDonationDate: String?): String {
        if (lastDonationDate == null || lastDonationDate == "Never" || lastDonationDate.isBlank()) return "Now"
        return try {
            val lastDate = dateFormatter.parse(lastDonationDate) ?: return "Now"
            val calendar = Calendar.getInstance()
            calendar.time = lastDate
            calendar.add(Calendar.DAY_OF_YEAR, 90)
            dateFormatter.format(calendar.time)
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getRemainingDays(lastDonationDate: String?): Long {
        if (lastDonationDate == null || lastDonationDate == "Never" || lastDonationDate.isBlank()) return 0
        return try {
            val lastDate = dateFormatter.parse(lastDonationDate) ?: return 0
            val calendar = Calendar.getInstance()
            calendar.time = lastDate
            calendar.add(Calendar.DAY_OF_YEAR, 90)

            val diff = calendar.timeInMillis - System.currentTimeMillis()
            if (diff <= 0) 0 else TimeUnit.MILLISECONDS.toDays(diff) + 1
        } catch (e: Exception) {
            0
        }
    }
}
