package com.qualifio.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.qualifio.Qualifio
import com.qualifio.views.QualifioInlineCampaignView

/**
 * Displays a Qualifio campaign inline in a Jetpack Compose.
 */
@Composable
public fun QualifioInlineCampaign(
    modifier: Modifier = Modifier,
    campaign: String = Qualifio.DEFAULT_CAMPAIGN,
    autoScrollOnPageLoadEnabled: Boolean = true,
) {
    var currentCampaign by remember { mutableStateOf(campaign) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            QualifioInlineCampaignView(
                context = context,
                campaign = campaign,
                autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabled,
            )
        },
        update = { view ->
            view.autoScrollOnPageLoadEnabled = autoScrollOnPageLoadEnabled

            if (campaign == currentCampaign) {
                return@AndroidView
            }

            currentCampaign = campaign
            view.loadCampaign(campaign)
        },
    )
}
