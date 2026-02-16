package com.qualifio.sample.ui.screens.settings.secure_token_encryption

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun SecureTokenEncryptionCard(
    viewModel: SecureTokenEncryptionViewModel = hiltViewModel(),
) {
    val secureTokenAlgorithm by viewModel.secureTokenAlgorithm.collectAsState()
    val secureTokenSecret by viewModel.secureTokenSecret.collectAsState()

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
                    text = stringResource(R.string.secure_token_encryption_title),
                    style = Typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_encryption_algorithm_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = secureTokenAlgorithm,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = stringResource(R.string.secure_token_encryption_secret_label),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = secureTokenSecret,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SecureTokenEncryptionCardPreview() {
    SecureTokenEncryptionCard()
}
