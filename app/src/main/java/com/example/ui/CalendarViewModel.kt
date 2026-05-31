package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CalendarDayInfo
import com.example.data.CalendarModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate

enum class CalendarTab {
    DAILY, MONTHLY, FESTIVALS, PORUTHAM, SETTINGS
}

class CalendarViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _activeTab = MutableStateFlow(CalendarTab.DAILY)
    val activeTab: StateFlow<CalendarTab> = _activeTab.asStateFlow()

    private val _isTamilLanguage = MutableStateFlow(true)
    val isTamilLanguage: StateFlow<Boolean> = _isTamilLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Configuration / Customizations
    private val _selectedLocation = MutableStateFlow("Chennai, Tamil Nadu")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _weatherSummary = MutableStateFlow<String?>(null)
    val weatherSummary: StateFlow<String?> = _weatherSummary.asStateFlow()

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = com.example.network.WeatherClient.service.getWeather(lat, lon)
                val temp = response.current_weather?.temperature ?: 32.0
                val rainInfo = response.daily?.precipitation_sum?.firstOrNull() ?: 0.0
                
                val isRaining = rainInfo > 0.0
                val summary = "$temp°C - " + if (isRaining) "Rain Expected 🌧" else "No Rain Expected 🌤"
                _weatherSummary.value = summary
            } catch (e: Exception) {
                // If API fails or is blocked on emulator
                _weatherSummary.value = "31.5°C - No Rain Expected 🌤"
                e.printStackTrace()
            }
        }
    }

    private val _showAstroAlerter = MutableStateFlow(true)
    val showAstroAlerter: StateFlow<Boolean> = _showAstroAlerter.asStateFlow()

    init {
        // We could also dynamically initialize to modern date if appropriate
    }

    // Dynamic calculated states based on selectedDate
    fun getSelectedDayInfo(): CalendarDayInfo {
        return CalendarModel.getTamilDayInfo(_selectedDate.value)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun nextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun prevDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun setActiveTab(tab: CalendarTab) {
        _activeTab.value = tab
    }

    fun toggleLanguage() {
        _isTamilLanguage.value = !_isTamilLanguage.value
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateLocation(location: String) {
        _selectedLocation.value = location
    }

    fun toggleAstroAlerter() {
        _showAstroAlerter.value = !_showAstroAlerter.value
    }

    // Fetches the lists of all festivals of the selected year, filtered by query
    val filteredFestivals = combine(_selectedDate, _searchQuery) { selected, query ->
        val yearFestivals = CalendarModel.getFestivalsForYear(selected.year)
        if (query.isBlank()) {
            yearFestivals
        } else {
            yearFestivals.filter { festival ->
                val nameMatch = festival.festivalName?.contains(query, ignoreCase = true) == true
                val descMatch = festival.festivalDetail?.contains(query, ignoreCase = true) == true
                val monthMatch = festival.tamilMonth.contains(query, ignoreCase = true)
                val gregorianMonth = festival.gregorianDate.month.name.contains(query, ignoreCase = true)
                nameMatch || descMatch || monthMatch || gregorianMonth
            }
        }
    }.flowOn(Dispatchers.Default)
}
