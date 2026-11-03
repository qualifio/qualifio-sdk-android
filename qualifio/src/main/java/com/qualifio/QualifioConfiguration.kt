package com.qualifio

import com.qualifio.internal.logger

public data class QualifioConfiguration(
    val domain: String
) {
    internal val isValid: Boolean
        get() {
            if (!HOST_REGEX.matches(domain)) {
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

    private companion object {
        @Suppress("detekt:MaxLineLength")
        private val HOST_REGEX = Regex(
            "^(https?://)?(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])(:[0-9]{1,5})?$",
        )
    }
}
