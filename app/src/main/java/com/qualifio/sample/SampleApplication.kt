package com.qualifio.sample

import android.app.Application
import com.qualifio.Qualifio
import com.qualifio.QualifioListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleApplication : Application(), QualifioListener {
    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        // Customise Qualifio.
        Qualifio.listener = this

        mainScope.launch {
            delay(2_000)
            Qualifio.launchCampaign()
        }
    }

//    override suspend fun generateSecureToken(url: Uri): String {
//        return ""
//    }
}
