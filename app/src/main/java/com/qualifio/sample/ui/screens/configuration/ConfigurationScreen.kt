package com.qualifio.sample.ui.screens.configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration
import com.qualifio.sample.BuildConfig
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val defaultDomain = stringResource(R.string.qualifio_services_domain)

    var domainInput by remember { mutableStateOf(defaultDomain) }
    var secretInput by remember { mutableStateOf(BuildConfig.SECURE_TOKEN_SECRET) }
    var isDomainError by remember { mutableStateOf(false) }
    var isSecretError by remember { mutableStateOf(false) }
    var useSecureToken by remember {
        mutableStateOf(BuildConfig.SECURE_TOKEN_SECRET.isNotBlank())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.85f),
            value = domainInput,
            onValueChange = {
                domainInput = it
                if (isDomainError && it.isNotBlank()) {
                    isDomainError = false
                }
            },
            isError = isDomainError,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.configuration_input),
                )
            },
            supportingText = {
                if (isDomainError) {
                    Text(
                        text = stringResource(R.string.configuration_invalid_domain),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Uri,
                autoCorrectEnabled = false,
            ),
        )

        Spacer(modifier = Modifier)

        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_preferences_secure_token_label),
                style = Typography.bodyLarge,
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = useSecureToken,
                onCheckedChange = { enabled ->
                    useSecureToken = enabled
                },
            )
        }

        if (useSecureToken) {
            Spacer(modifier = Modifier)

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.85f),
                value = secretInput,
                onValueChange = {
                    secretInput = it
                    if (isSecretError && it.isNotBlank()) {
                        isSecretError = false
                    }
                },
                isError = isSecretError,
                singleLine = true,
                label = {
                    Text(
                        text = stringResource(R.string.configuration_secret_input),
                    )
                },
                supportingText = {
                    if (isSecretError) {
                        Text(
                            text = stringResource(R.string.configuration_invalid_secret),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                    autoCorrectEnabled = false,
                ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(0.85f),
            onClick = {
                if (!viewModel.validateDomain(domainInput)) {
                    isDomainError = true
                    return@Button
                }

                if(useSecureToken && secretInput.isBlank()) {
                    isSecretError = true
                    return@Button
                }

                Qualifio.configure(
                    context = context,
                    configuration = QualifioConfiguration(domainInput),
                )

                if (useSecureToken) {
                    viewModel.updateConfiguration(domainInput, secretInput)
                } else {
                    viewModel.updateConfiguration(domainInput, null)
                }
            },
            content = {
                Text(
                    text = stringResource(R.string.configuration_button),
                )
            },
        )
    }
}

@Preview
@Composable
fun ConfigurationScreenPreview() {
    ConfigurationScreen()
}
