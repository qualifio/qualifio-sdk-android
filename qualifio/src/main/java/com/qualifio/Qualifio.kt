package com.qualifio

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.qualifio.internal.coroutines.toCallbackFunction
import com.qualifio.internal.logger
import com.qualifio.internal.utils.applicationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public typealias QualifioSecureTokenGenerator = suspend (String) -> String?
public typealias QualifioCustomTabsCustomizer = (builder: CustomTabsIntent.Builder) -> Unit

public object Qualifio {

    private var context: WeakReference<Context>? = null
    private var secureTokenGenerator: QualifioSecureTokenGenerator? = null
    private var customTabsCustomizer: QualifioCustomTabsCustomizer? = null

    @JvmStatic
    public var configuration: QualifioConfiguration? = null
        private set

    @JvmStatic
    public val isConfigured: Boolean
        get() = configuration != null

    @JvmStatic
    public fun configure(context: Context) {
        val configuration = loadConfigurationFromResources(context)
        configure(context, configuration)
    }

    @JvmStatic
    public fun configure(context: Context, configuration: QualifioConfiguration) {
        if (isConfigured) {
            logger.info("Qualifio can only be configured once.")
            return
        }

        if (configuration.domain.isBlank()) {
            throw IllegalArgumentException("Qualifio cannot be configured without a domain.")
        }

        if (!configuration.isValid) {
            throw IllegalArgumentException("Qualifio cannot be configured without a valid set of configurations.")
        }

        logger.hasDebugLoggingEnabled = hasDebugLoggingEnabled(context)

        this.context = WeakReference(context.applicationContext)
        this.configuration = configuration
    }

    @JvmSynthetic
    public fun setSecureTokenProvider(generator: QualifioSecureTokenGenerator?) {
        secureTokenGenerator = generator
    }

    @JvmStatic
    public fun setSecureTokenProvider(provider: QualifioSecureTokenProvider?) {
        secureTokenGenerator = provider?.let { p ->
            { campaign: String ->
                suspendCancellableCoroutine { continuation ->
                    p.generateSecureToken(campaign, object : QualifioCallback<String?> {
                        override fun onSuccess(result: String?) = continuation.resume(result)
                        override fun onFailure(e: Exception) = continuation.resumeWithException(e)
                    })
                }
            }
        }
    }

    @JvmStatic
    public fun setCustomTabsCustomizer(customizer: QualifioCustomTabsCustomizer?) {
        customTabsCustomizer = customizer
    }

    public suspend fun launchCampaign(campaign: String = "sdk"): Unit = withContext(Dispatchers.IO) {
        val configuration = configuration
            ?: throw IllegalStateException("No configuration set. Please call Qualifio.configure()")

        if (campaign.isBlank()) {
            throw IllegalArgumentException("Invalid campaign parameter.")
        }

        var url = determineCampaignUrl(configuration, campaign.trim())
        logger.debug("Preparing campaign $url")

        try {
            val timeoutMillis = getSecureTokenGeneratorTimeoutMilliseconds(requireContext())
            val token = withTimeout(timeoutMillis) {
                secureTokenGenerator?.invoke(campaign)
            }

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
                .apply { customTabsCustomizer?.invoke(this) }
                .build()
                .apply { this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                .launchUrl(requireContext(), url)
        } catch (e: Exception) {
            throw Exception("Failed to launch the campaign browser.", e)
        }
    }

    @JvmStatic
    @JvmOverloads
    public fun launchCampaign(group: String = "sdk", callback: QualifioCallback<Unit>): Unit =
        toCallbackFunction(::launchCampaign)(group, callback::onSuccess, callback::onFailure)

    internal fun requireContext(): Context {
        return context?.get() ?: error("Cannot find the context for Qualifio.")
    }

    private fun hasDebugLoggingEnabled(context: Context): Boolean {
        val info = context.packageManager.applicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )

        val metadata = info.metaData ?: bundleOf()

        return metadata.getBoolean("com.qualifio.debug_logging_enabled", false)
    }

    private fun getSecureTokenGeneratorTimeoutMilliseconds(context: Context): Long {
        val info = context.packageManager.applicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )

        val metadata = info.metaData ?: bundleOf()
        val timeoutMillis = metadata.getInt("com.qualifio.secure_token_generator_timeout_milliseconds", 30_000)

        return timeoutMillis.toLong()
    }

    private fun loadConfigurationFromResources(context: Context): QualifioConfiguration {
        val domain = try {
            context.getString(R.string.qualifio_services_domain)
        } catch (_: Resources.NotFoundException) {
            error("Qualifio domain unavailable.")
        }

        return QualifioConfiguration(domain)
    }

    private fun determineCampaignUrl(configuration: QualifioConfiguration, campaign: String): Uri {
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
