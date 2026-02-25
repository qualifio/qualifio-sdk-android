package com.qualifio.sample.ui.screens.secure_token_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qualifio.sample.R
import com.qualifio.sample.ui.screens.secure_token_fields.components.KeyValuePairRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureTokenFieldsScreen(
    viewModel: SecureTokenFieldsViewModel = hiltViewModel(),
    innerPadding: PaddingValues
) {
    val fields by viewModel.secureTokenFields.collectAsState()

    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }

    var keyError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .consumeWindowInsets(innerPadding)
            .imePadding(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(fields?.entries?.toList() ?: emptyList()) { entry ->
                KeyValuePairRow(
                    key = entry.key,
                    value = entry.value,
                    onDelete = { viewModel.deleteSecureTokenField(entry.key) },
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        OutlinedTextField(
            value = keyInput,
            onValueChange = {
                keyInput = it
                keyError = false
            },
            label = { Text(text = stringResource(R.string.secure_token_fields_key_label)) },
            isError = keyError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valueInput,
            onValueChange = { valueInput = it },
            label = { Text(text = stringResource(R.string.secure_token_fields_value_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (keyInput.isBlank() || fields?.containsKey(keyInput) == true) {
                    keyError = true
                    return@Button
                }

                focusManager.clearFocus()

                viewModel.addSecureTokenField(keyInput, valueInput)

                keyInput = ""
                valueInput = ""
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.secure_token_fields_add_field_button))
        }
    }
}

@Preview
@Composable
fun SecureTokenFieldsScreenPreview() {
    SecureTokenFieldsScreen(innerPadding = PaddingValues(0.dp))
}
