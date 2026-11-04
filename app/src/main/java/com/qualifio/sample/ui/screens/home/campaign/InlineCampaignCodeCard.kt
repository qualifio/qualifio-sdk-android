package com.qualifio.sample.ui.screens.home.campaign

import android.view.View
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.qualifio.Qualifio
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography
import com.qualifio.views.QualifioInlineCampaignView

@Composable
fun InlineCampaignCodeCard() {
    val rootView = LocalView.current
    val campaignViewId = remember { View.generateViewId() }

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
                    text = stringResource(R.string.home_inline_campaign_code_title),
                    style = Typography.bodyLarge,
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    QualifioInlineCampaignView(
                        context = context,
                        campaign = Qualifio.DEFAULT_CAMPAIGN,
                        autoScrollOnPageLoadEnabled = true,
                    ).apply {
                        id = campaignViewId
                    }
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        rootView.findViewById<QualifioInlineCampaignView>(
                            campaignViewId,
                        )?.loadCampaign(Qualifio.DEFAULT_CAMPAIGN)
                    },
                ) {
                    Text(text = stringResource(R.string.home_inline_campaign_default_button))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        rootView.findViewById<QualifioInlineCampaignView>(
                            campaignViewId,
                        )?.loadCampaign("sdk-xmas")
                    },
                ) {
                    Text(text = stringResource(R.string.home_inline_campaign_xmas_button))
                }
            }
        }
    }
}

@Preview
@Composable
fun InlineCampaignCodeCardPreview() {
    InlineCampaignCodeCard()
}
