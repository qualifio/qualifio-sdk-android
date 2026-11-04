package com.qualifio.sample.ui.screens.secure_token_fields

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecureTokenFieldsViewModel @Inject constructor(
    private val dataStore: SampleDataStore,
) : ViewModel() {
    val secureTokenFields = dataStore.secureTokenFieldsStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null,
        )

    fun addSecureTokenField(key: String, value: String) {
        viewModelScope.launch {
            val currentFields = secureTokenFields.value ?: emptyMap()

            val updatedFields = currentFields.toMutableMap().apply {
                put(key.trim(), value.trim())
            }

            dataStore.updateSecureTokenFields(updatedFields)
        }
    }

    fun deleteSecureTokenField(key: String) {
        viewModelScope.launch {
            val currentFields = secureTokenFields.value ?: return@launch

            val updatedFields = currentFields.toMutableMap().apply {
                remove(key)
            }

            dataStore.updateSecureTokenFields(
                updatedFields,
            )
        }
    }
}
