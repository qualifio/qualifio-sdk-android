package com.qualifio.sample.ui.screens.settings.secure_token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecureTokenViewModel @Inject constructor(
    private val dataStore: SampleDataStore,
) : ViewModel() {
    val useSecureToken = dataStore.useSecureTokenStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false,
        )

    val secureTokenAlgorithm = dataStore.secureTokenAlgorithmStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "",
        )

    val secureTokenSecret = dataStore.secureTokenSecretStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "",
        )

    val secureTokenSubject = dataStore.secureTokenSubjectStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "",
        )

    fun updateUseSecureToken(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.updateUseSecureToken(enabled)
        }
    }

    fun updateSecureTokenSecret(secret: String) {
        viewModelScope.launch {
            dataStore.updateSecureTokenSecret(secret)
        }
    }

    fun updateSecureTokenSubject(secret: String) {
        viewModelScope.launch {
            dataStore.updateSecureTokenSubject(secret)
        }
    }
}