package com.qualifio

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.qualifio.internal.SecureTokenGeneratorUnavailableException

public interface QualifioListener {
    // TODO: the return needs to be nullable
    public suspend fun generateSecureToken(url: Uri): String {
        throw SecureTokenGeneratorUnavailableException()
    }

    // TODO: add callback

    public fun customizeCustomTabsIntent(builder: CustomTabsIntent.Builder) {
        // No customisations by default.
    }
}
