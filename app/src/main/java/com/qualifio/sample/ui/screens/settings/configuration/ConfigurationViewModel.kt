package com.qualifio.sample.ui.screens.settings.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qualifio.sample.storage.SampleDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dataStore: SampleDataStore,
) : ViewModel() {
    val domain = dataStore.domainStream
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null,
        )
}
