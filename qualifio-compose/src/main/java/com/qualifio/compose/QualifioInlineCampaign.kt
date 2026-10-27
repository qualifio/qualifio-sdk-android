package com.qualifio.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.qualifio.Qualifio
import com.qualifio.views.QualifioInlineCampaignView

/**
 * Displays a Qualifio campaign inline in a Jetpack Compose composable.
 */
@Composable
public fun QualifioInlineCampaign(
    modifier: Modifier = Modifier,
    campaign: String = Qualifio.DEFAULT_CAMPAIGN,
    autoScrollOnPageLoadEnabled: Boolean = true,
) {
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

            if (view.campaign != campaign) {
                view.loadCampaign(campaign)
            }
        },
    )
}
