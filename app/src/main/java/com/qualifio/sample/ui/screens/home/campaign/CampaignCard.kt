package com.qualifio.sample.ui.screens.home.campaign

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.Qualifio
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun CampaignCard() {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

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
                    painter = painterResource(R.drawable.ic_news_24),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.home_campaign_title),
                    style = Typography.bodyLarge,
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = {
                    text = it
                    if (isError && it.isNotBlank()) {
                        isError = false
                    }
                },
                isError = isError,
                label = {
                    Text(
                        text = stringResource(R.string.home_campaign_input),
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                    autoCorrectEnabled = false,
                ),
                singleLine = true,
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Qualifio.launchCampaign(text)
                },
                content = {
                    Text(
                        text = stringResource(R.string.home_campaign_button),
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun CampaignCardPreview() {
    CampaignCard()
}
