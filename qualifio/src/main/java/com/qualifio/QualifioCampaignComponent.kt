package com.qualifio

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.qualifio.internal.coroutines.toCallbackFunction
import com.qualifio.internal.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public class QualifioCampaignComponent internal constructor() {
    public val hasSecureTokenProvider: Boolean
        get() = secureTokenProvider != null

    private var secureTokenProvider: (suspend (uri: Uri) -> String?)? = null
    private var customTabsIntentBuilderCustomizer: (CustomTabsIntent.Builder) -> Unit = {
        // No customizations by default.
    }

    public fun setSecureTokenProvider(
        provider: suspend (uri: Uri) -> String?
    ) {
        secureTokenProvider = provider
    }

    public fun setSecureTokenProvider(provider: QualifioCallbackFunction<Uri, String?>) {
        setSecureTokenProvider { uri ->
            suspendCancellableCoroutine { continuation ->
                provider.invoke(uri, object : QualifioCallback<String?> {
                    override fun onSuccess(result: String?) {
                        continuation.resume(result)
                    }

                    override fun onFailure(e: Exception) {
                        continuation.resumeWithException(e)
                    }
                })
            }
        }
    }

    public fun setCustomTabsIntentBuilderCustomizer(
        customizer: ((builder: CustomTabsIntent.Builder) -> Unit)
    ) {
        customTabsIntentBuilderCustomizer = customizer
    }

    public suspend fun launch(campaign: String = "sdk"): Unit = withContext(Dispatchers.IO) {
        val configuration = Qualifio.configuration
            ?: throw IllegalStateException("No configuration set. Please call Qualifio.configure()")

        if (campaign.isBlank()) {
            throw IllegalArgumentException("Invalid campaign parameter.")
        }

        var url = determineUrl(configuration, campaign.trim())
        logger.debug("Preparing campaign $url")

        try {
            val token = secureTokenProvider?.invoke(url)
            if (token != null) {
                logger.debug("Setting the provided secure token.")
                url = url.buildUpon().appendQueryParameter("qual_token", token).build()
            }
        } catch (e: Exception) {
            logger.error("Failed to acquire a secure token.", e)
        }

        try {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .apply { customTabsIntentBuilderCustomizer(this) }
                .build()
                .apply { this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                .launchUrl(Qualifio.requireContext(), url)
        } catch (e: Exception) {
            throw Exception("Failed to launch the campaign browser.", e)
        }
    }

    @JvmOverloads
    public fun launch(group: String = "sdk", callback: QualifioCallback<Unit>): Unit =
        toCallbackFunction(::launch)(group, callback::onSuccess, callback::onFailure)

    private fun determineUrl(configuration: QualifioConfiguration, campaign: String): Uri {
        val campaignPathComponents = campaign.split("/")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return configuration.host.toUri()
            .buildUpon()
            .apply {
                campaignPathComponents.forEach {
                    appendPath(it)
                }
            }
            .build()
    }
}
