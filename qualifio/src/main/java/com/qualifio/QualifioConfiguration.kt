package com.qualifio

import androidx.core.net.toUri
import com.qualifio.internal.logger

public data class QualifioConfiguration(
    val domain: String
) {
    internal val isValid: Boolean
        get() {
            val hostUri = host.toUri()
            if (hostUri.scheme == null || hostUri.host == null || hostUri.query != null) {
                logger.debug("Invalid domain value.")
                return false
            }

            return true
        }

    internal val host: String
        get() {
            if (!domain.startsWith("https://") && !domain.startsWith("http://")) {
                return "https://${domain}"
            }

            return domain
        }
}
