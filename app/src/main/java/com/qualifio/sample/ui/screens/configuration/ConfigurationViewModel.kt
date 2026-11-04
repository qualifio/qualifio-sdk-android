package com.qualifio.sample.ui.screens.configuration

import androidx.core.net.toUri
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
                dataStore.updateUseSecureToken(true)
            }
            dataStore.updateIsConfigurationFinished(true)
        }
    }

    fun validateDomain(domain: String): Boolean {
        if (domain.isBlank()) {
            return false
        }

        val hostUri = normalizeDomain(domain).toUri()

        return !(hostUri.scheme == null || hostUri.host == null || hostUri.query != null)
    }

    private fun normalizeDomain(domain: String): String {
        return if (!domain.startsWith("https://") && !domain.startsWith("http://")) {
            "https://$domain"
        } else {
            domain
        }
    }
}
