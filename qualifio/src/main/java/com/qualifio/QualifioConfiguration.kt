package com.qualifio

import com.qualifio.internal.logger

private val DOMAIN_REGEX = "^(http(s)?://)?([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}(\\d+)?$".toRegex()

public data class QualifioConfiguration(
    val domain: String
) {
    internal val isValid: Boolean
        get() {
            if (!DOMAIN_REGEX.matches(domain)) {
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
