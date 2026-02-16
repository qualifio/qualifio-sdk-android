package com.qualifio.sample.ui.screens.settings.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val dataStore: SampleDataStore,
) : ViewModel() {
    val useSecureToken = dataStore.useSecureTokenStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false,
        )

    val showCampaignAtLaunch = dataStore.showCampaignAtLaunchStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true,
        )

    fun updateUseSecureToken(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.updateUseSecureToken(enabled)
        }
    }

    fun updateShowCampaignAtLaunch(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.updateShowCampaignAtLaunch(enabled)
        }
    }
}
