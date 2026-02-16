package com.qualifio.sample.ui.screens.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val DOMAIN_REGEX =
    "^(http(s)?://)?([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}(\\d+)?$".toRegex()

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dataStore: SampleDataStore
): ViewModel() {
    fun updateConfiguration(domain: String, secret: String?) {
        viewModelScope.launch {
            dataStore.updateDomain(domain)
            if(secret != null) {
                dataStore.updateSecureTokenSecret(secret)
            }
            dataStore.updateIsConfigurationFinished(true)
        }
    }

    fun validateDomain(domain: String): Boolean {
        return (!domain.isBlank() && DOMAIN_REGEX.matches(domain))
    }
}
