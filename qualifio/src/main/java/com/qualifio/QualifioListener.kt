package com.qualifio

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.qualifio.internal.SecureTokenGeneratorUnavailableException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public interface QualifioListener {
    public suspend fun generateSecureToken(url: Uri): String? {
        return suspendCancellableCoroutine { continuation ->
            generateSecureToken(url, object : QualifioCallback<String?> {
                override fun onSuccess(result: String?) {
                    continuation.resume(result)
                }

                override fun onFailure(e: Exception) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    public fun generateSecureToken(url: Uri, callback: QualifioCallback<String?>) {
        callback.onSuccess(null)
    }

    public fun customizeCustomTabsIntent(builder: CustomTabsIntent.Builder) {
        // No customisations by default.
    }
}
