package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PoruthamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PoruthamState {
    INPUT, LOADING, RESULT
}

class PoruthamViewModel(private val repository: PoruthamRepository) : ViewModel() {
    private val _currentState = MutableStateFlow(PoruthamState.INPUT)
    val currentState: StateFlow<PoruthamState> = _currentState.asStateFlow()

    private val _loadingMessage = MutableStateFlow("Preparing Horoscope")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    private val _groomName = MutableStateFlow("")
    val groomName: StateFlow<String> = _groomName.asStateFlow()
    
    private val _brideName = MutableStateFlow("")
    val brideName: StateFlow<String> = _brideName.asStateFlow()

    fun updateGroomName(name: String) { _groomName.value = name }
    fun updateBrideName(name: String) { _brideName.value = name }

    private val _harmonyScore = MutableStateFlow(0)
    val harmonyScore: StateFlow<Int> = _harmonyScore.asStateFlow()

    fun checkCompatibility(groomName: String, brideName: String) {
        if (groomName.isBlank() || brideName.isBlank()) return
        
        viewModelScope.launch {
            _currentState.value = PoruthamState.LOADING
            
            _loadingMessage.value = "Preparing Horoscope"
            delay(1000)
            _loadingMessage.value = "Calculating Nakshatra"
            delay(1000)
            _loadingMessage.value = "Calculating Rasi"
            delay(1000)
            _loadingMessage.value = "Checking Compatibility"
            delay(1000)
            _loadingMessage.value = "Generating Report"
            delay(500)
            
            // Mock score calculation based on string length to return something realistic
            val combinedLength = groomName.length + brideName.length
            val score = 75 + (combinedLength % 20) // Returns 75-94 usually
            _harmonyScore.value = score
            
            _currentState.value = PoruthamState.RESULT
        }
    }
    
    fun reset() {
        _currentState.value = PoruthamState.INPUT
        _groomName.value = ""
        _brideName.value = ""
    }

    fun saveReport() {
        viewModelScope.launch {
            repository.saveReport(_groomName.value, _brideName.value, _harmonyScore.value)
        }
    }
}

class PoruthamViewModelFactory(private val repository: PoruthamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoruthamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoruthamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
