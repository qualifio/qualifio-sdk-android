package com.qualifio.sample.ui.screens.configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration
import com.qualifio.sample.R

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var domainInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

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
                if (isError && it.isNotBlank()) {
                    isError = false
                }
            },
            isError = isError,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.configuration_input),
                )
            },
            supportingText = {
                if (isError) {
                    Text(
                        text = stringResource(R.string.configuration_invalid_domain),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(0.85f),
            onClick = {
                if (!viewModel.validateDomain(domainInput)) {
                    isError = true
                    return@Button
                }

                Qualifio.configure(
                    context = context,
                    configuration = QualifioConfiguration(domainInput),
                )

                viewModel.updateDomain(domainInput)
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
