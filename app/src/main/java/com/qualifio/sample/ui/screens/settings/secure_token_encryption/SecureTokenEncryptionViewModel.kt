package com.qualifio.sample.ui.screens.settings.secure_token_encryption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SecureTokenEncryptionViewModel @Inject constructor(
    private val dataStore: SampleDataStore,
) : ViewModel() {
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
}
