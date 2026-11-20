package com.qualifio.sample.ui.screens.configuration

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val DOMAIN_REGEX =
    "^(https?://)?(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])(:[0-9]{1,5})?$".toRegex()

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dataStore: SampleDataStore
): ViewModel() {
    fun updateConfiguration(domain: String, secret: String?) {
        viewModelScope.launch {
            dataStore.updateDomain(domain)
            if(secret != null) {
                dataStore.updateSecureTokenSecret(secret)
                dataStore.updateUseSecureToken(true)
            }
            dataStore.updateIsConfigurationFinished(true)
        }
    }

    fun validateDomain(domain: String): Boolean {
        return DOMAIN_REGEX.matches(domain)
    }
}
