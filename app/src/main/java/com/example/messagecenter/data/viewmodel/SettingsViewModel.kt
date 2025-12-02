package com.example.messagecenter.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagecenter.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val autoDark: StateFlow<Boolean> = preferencesRepository.autoDark
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false
        )

    val enableDevMode: StateFlow<Boolean> = preferencesRepository.enableDevMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false
        )

    val enableReceiving: StateFlow<Boolean> = preferencesRepository.enableReceiving
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false
        )


    fun toggleAutoDark(isChecked: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveAutoDarkPreference(isChecked)
        }
    }

    fun toggleEnableDevMode(isChecked: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveEnableDevModePreference(isChecked)
        }
    }

    fun toggleEnableReceiving(isChecked: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveEnableReceivingPreference(isChecked)
        }
    }
}