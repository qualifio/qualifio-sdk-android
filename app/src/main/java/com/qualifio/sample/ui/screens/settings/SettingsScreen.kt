package com.qualifio.sample.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.sample.BuildConfig
import com.qualifio.sample.ui.screens.settings.configuration.ConfigurationCard
import com.qualifio.sample.ui.screens.settings.preferences.PreferencesCard
import com.qualifio.sample.ui.screens.settings.secure_token.SecureTokenCard
import com.qualifio.sample.ui.theme.SampleTheme

@Composable
internal fun SettingsScreen(
    onSecureTokenFieldsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ConfigurationCard()

        SecureTokenCard { onSecureTokenFieldsClick() }

        PreferencesCard()

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Version " + BuildConfig.VERSION_NAME,
            )
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    SampleTheme {
        SettingsScreen(onSecureTokenFieldsClick = {})
    }
}
