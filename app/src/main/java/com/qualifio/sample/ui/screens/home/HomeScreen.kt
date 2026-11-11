package com.qualifio.sample.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.qualifio.sample.R
import com.qualifio.sample.SAMPLE_CAMPAIGN
import com.qualifio.sample.ui.screens.home.campaign.CampaignCard
import com.qualifio.sample.ui.screens.home.campaign.InlineCampaignCodeCard
import com.qualifio.sample.ui.screens.home.campaign.InlineCampaignComposeCard
import com.qualifio.sample.ui.screens.home.campaign.InlineCampaignXmlCard
import com.qualifio.sample.ui.screens.home.components.ContentCard

@Composable
internal fun HomeScreen() {
    var inlineCampaign by rememberSaveable { mutableStateOf(SAMPLE_CAMPAIGN) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ContentCard(
            title = stringResource(R.string.home_content_title),
            content = stringResource(R.string.home_content_content),
        )

        CampaignCard(
            onLoadInlineCampaign = { inlineCampaign = it },
        )

        InlineCampaignXmlCard(campaign = inlineCampaign)

        InlineCampaignCodeCard(campaign = inlineCampaign)

        InlineCampaignComposeCard(campaign = inlineCampaign)

        ContentCard(
            title = stringResource(R.string.home_content_title),
            content = stringResource(R.string.home_content_content),
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
