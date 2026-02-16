package com.qualifio.sample.ui.screens.settings.secure_token

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val useSecurityKey by viewModel.useSecureToken.collectAsState()
    val secureTokenAlgorithm by viewModel.secureTokenAlgorithm.collectAsState()
    val secureTokenSecret by viewModel.secureTokenSecret.collectAsState()
    val secureTokenSubject by viewModel.secureTokenSubject.collectAsState()
    var isEditingSecret by remember { mutableStateOf(false) }
    var isEditingSubject by remember { mutableStateOf(false) }
    var secret by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

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
                    checked = useSecurityKey,
                    onCheckedChange = { enabled ->
                        viewModel.updateUseSecureToken(enabled)
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_secret_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(0.95f))

                if (isEditingSecret) {
                    OutlinedTextField(
                        value = secret,
                        onValueChange = { secret = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Uri,
                            autoCorrectEnabled = false,
                        ),
                    )

                    Spacer(modifier = Modifier.weight(0.05f))

                    IconButton(
                        onClick = {
                            viewModel.updateSecureTokenSecret(secret)
                            isEditingSecret = false
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check_24),
                            contentDescription = "Save",
                        )
                    }
                } else {
                    Text(
                        text = secureTokenSecret,
                        overflow = TextOverflow.MiddleEllipsis,
                    )

                    Spacer(modifier = Modifier.weight(0.05f))

                    IconButton(
                        onClick = { isEditingSecret = true },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit_24),
                            contentDescription = "Edit",
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_subject_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(0.95f))

                if (isEditingSubject) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Uri,
                            autoCorrectEnabled = false,
                        ),
                    )

                    Spacer(modifier = Modifier.weight(0.05f))

                    IconButton(
                        onClick = {
                            viewModel.updateSecureTokenSubject(subject)
                            isEditingSubject = false
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check_24),
                            contentDescription = "Save",
                        )
                    }
                } else {
                    Text(
                        text = secureTokenSubject,
                        overflow = TextOverflow.MiddleEllipsis,
                    )

                    Spacer(modifier = Modifier.weight(0.05f))

                    IconButton(
                        onClick = { isEditingSubject = true },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit_24),
                            contentDescription = "Edit",
                        )
                    }
                }
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
}

@Preview
@Composable
private fun SecureTokenCardPreview() {
    SecureTokenCard {}
}
