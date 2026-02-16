package com.qualifio.sample.ui.screens.settings.secure_token

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun SecureTokenCard(
    viewModel: SecureTokenViewModel = hiltViewModel(),
    onClick: () -> Unit,
) {
    val context = LocalContext.current

    val useSecurityToken by viewModel.useSecureToken.collectAsState()
    val secureTokenAlgorithm by viewModel.secureTokenAlgorithm.collectAsState()
    val secureTokenSecret by viewModel.secureTokenSecret.collectAsState()
    val secureTokenSubject by viewModel.secureTokenSubject.collectAsState()
    var showSecretEditDialog by remember { mutableStateOf(false) }
    var showSubjectEditDialog by remember { mutableStateOf(false) }
    val toastText = stringResource(R.string.secure_token_secret_blank_toast)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_encryption_24),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.secure_token_title),
                    style = Typography.bodyLarge,
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = useSecurityToken,
                    onCheckedChange = { enabled ->
                        if (enabled && secureTokenSecret.isBlank()) {
                            Toast.makeText(
                                context,
                                toastText,
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            viewModel.updateUseSecureToken(enabled)
                        }
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_algorithm_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = secureTokenAlgorithm,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { showSecretEditDialog = true }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_secret_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(0.95f))

                Text(
                    text = secureTokenSecret,
                    overflow = TextOverflow.MiddleEllipsis,
                )

                Spacer(modifier = Modifier.weight(0.05f))

                Icon(
                    painter = painterResource(R.drawable.ic_edit_24),
                    contentDescription = "Edit",
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { showSubjectEditDialog = true }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_subject_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(0.95f))

                Text(
                    text = secureTokenSubject,
                    overflow = TextOverflow.MiddleEllipsis,
                )

                Spacer(modifier = Modifier.weight(0.05f))

                Icon(
                    painter = painterResource(R.drawable.ic_edit_24),
                    contentDescription = "Edit",
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_security_field_add_24),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.secure_token_fields_title),
                    style = Typography.bodyLarge,
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right_24),
                    contentDescription = null,
                )
            }
        }
    }

    if (showSecretEditDialog) {
        var editSecretInput by remember { mutableStateOf(secureTokenSecret) }

        AlertDialog(
            onDismissRequest = { showSecretEditDialog = false },
            title = { Text(stringResource(R.string.secure_token_edit_secret_title)) },
            text = {
                OutlinedTextField(
                    value = editSecretInput,
                    onValueChange = { editSecretInput = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSecureTokenSecret(editSecretInput)
                        showSecretEditDialog = false
                    },
                ) {
                    Text(stringResource(R.string.secure_token_edit_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecretEditDialog = false }) {
                    Text(stringResource(R.string.secure_token_edit_dialog_cancel))
                }
            },
        )
    }

    if (showSubjectEditDialog) {
        var editSubjectInput by remember { mutableStateOf(secureTokenSubject) }

        AlertDialog(
            onDismissRequest = { showSubjectEditDialog = false },
            title = { Text(stringResource(R.string.secure_token_edit_subject_title)) },
            text = {
                OutlinedTextField(
                    value = editSubjectInput,
                    onValueChange = { editSubjectInput = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSecureTokenSubject(editSubjectInput)
                        showSubjectEditDialog = false
                    },
                ) {
                    Text(stringResource(R.string.secure_token_edit_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubjectEditDialog = false }) {
                    Text(stringResource(R.string.secure_token_edit_dialog_cancel))
                }
            },
        )
    }
}

@Preview
@Composable
private fun SecureTokenCardPreview() {
    SecureTokenCard {}
}
