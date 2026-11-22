package com.qualifio.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.qualifio.Qualifio
import com.qualifio.sample.storage.SampleDataStore
import com.qualifio.sample.ui.navigation.NavigationRoot
import com.qualifio.sample.ui.theme.SampleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: SampleDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavigationRoot()
                }
            }
        }

        if (savedInstanceState == null) {
            showCampaignAtLaunch()
        }
    }

    private fun showCampaignAtLaunch() {
        lifecycleScope.launch {
            val configurationFinished = dataStore.isConfigurationFinishedStream.first()
            val showCampaignAtLaunch = dataStore.showCampaignAtLaunchStream.first()
            if (!configurationFinished || !showCampaignAtLaunch) return@launch

            delay(2_000)

            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return@launch

            try {
                Qualifio.launchCampaign(SAMPLE_CAMPAIGN)
            } catch (e: Exception) {
                Timber.e(e, "Failed to launch campaign")
            }
        }
    }
}
