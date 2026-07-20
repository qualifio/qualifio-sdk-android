package com.qualifio.sample.ui.screens.home.campaign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.Qualifio
import com.qualifio.compose.QualifioInlineCampaign
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun CampaignInlineComposeCard() {
    var campaign by remember { mutableStateOf(Qualifio.DEFAULT_CAMPAIGN) }

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
                    text = stringResource(R.string.home_campaign_inline_compose_title),
                    style = Typography.bodyLarge,
                )
            }

            QualifioInlineCampaign(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFF9800),
                    )
                    .padding(8.dp),
                campaign = campaign,
                autoScrollOnPageLoadEnabled = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        campaign = Qualifio.DEFAULT_CAMPAIGN
                    },
                ) {
                    Text(text = stringResource(R.string.home_campaign_inline_default_button))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        campaign = "sdk-xmas"
                    },
                ) {
                    Text(text = stringResource(R.string.home_campaign_inline_xmas_button))
                }
            }
        }
    }
}

@Preview
@Composable
fun CampaignInlineComposeCardPreview() {
    CampaignInlineComposeCard()
}
