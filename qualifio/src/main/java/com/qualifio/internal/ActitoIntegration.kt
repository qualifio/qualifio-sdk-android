package com.qualifio.internal

import androidx.annotation.Keep
import com.qualifio.Qualifio

@Keep
public class ActitoIntegration {
    public suspend fun launchCampaign(campaign: String) {
        Qualifio.campaign().launch(campaign)
    }
}
