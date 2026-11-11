package com.qualifio.sample.ui.screens.home.campaign

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.compose.QualifioInlineCampaign
import com.qualifio.sample.R
import com.qualifio.sample.SAMPLE_CAMPAIGN
import com.qualifio.sample.ui.theme.Typography

@Composable
fun InlineCampaignComposeCard(
    campaign: String,
) {
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
                    text = stringResource(R.string.home_inline_campaign_compose_title),
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
        }
    }
}

@Preview
@Composable
fun InlineCampaignComposeCardPreview() {
    InlineCampaignComposeCard(campaign = SAMPLE_CAMPAIGN)
}
