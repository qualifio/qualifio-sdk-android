package com.qualifio

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.qualifio.internal.logger
import com.qualifio.internal.utils.applicationInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

public object Qualifio {
    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    private var context: WeakReference<Context>? = null
    private var weakListener: WeakReference<QualifioListener>? = null

    @JvmStatic
    public var configuration: QualifioConfiguration? = null
        private set

    @JvmStatic
    public val isConfigured: Boolean
        get() = configuration != null

    @JvmStatic
    public var listener: QualifioListener?
        get() = weakListener?.get()
        set(value) {
            weakListener = WeakReference(value)
        }

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

    @JvmStatic
    @JvmOverloads
    public fun launchCampaign(campaign: String = "sdk") {
        if (!isConfigured) {
            logger.warning("No configuration set. Please call Qualifio.configure()")
            return
        }

        val configuration = configuration ?: run {
            logger.warning("No configuration set. Please call Qualifio.configure()")
            return
        }

        var url = determineCampaignUrl(configuration, campaign)
        logger.debug("Preparing campaign $url")

        coroutineScope.launch {
            try {
                val token = listener?.generateSecureToken(url)
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
                    .apply { listener?.customizeCustomTabsIntent(this) }
                    .build()
                    .apply { this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                    .launchUrl(requireContext(), url)
            } catch (e: Exception) {
                logger.error("Failed to launch the campaign browser.", e)
            }
        }
    }

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

    private fun loadConfigurationFromResources(context: Context): QualifioConfiguration {
        val domain = try {
            context.getString(R.string.qualifio_services_domain)
        } catch (_: Resources.NotFoundException) {
            error("Qualifio domain unavailable.")
        }

        return QualifioConfiguration(domain)
    }

    private fun determineCampaignUrl(configuration: QualifioConfiguration, campaign: String): Uri {
        // TODO: validate URL format (campaign parameter)

        return configuration.host.toUri()
            .buildUpon()
            .appendPath(campaign)
            .build()

        // return "https://notificare1.qualifioapp.com/quiz/1711590_2956/prefill-secure-token-.html".toUri()
    }
}
