package com.qualifio

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.core.os.bundleOf
import com.qualifio.internal.logger
import com.qualifio.internal.utils.applicationInfo
import java.lang.ref.WeakReference

public object Qualifio {

    private val campaignComponent by lazy {
        QualifioCampaignComponent()
    }

    private var context: WeakReference<Context>? = null

    @JvmStatic
    public var configuration: QualifioConfiguration? = null
        private set

    @JvmStatic
    public val isConfigured: Boolean
        get() = configuration != null

    @JvmStatic
    public fun campaign(): QualifioCampaignComponent = campaignComponent

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
}
