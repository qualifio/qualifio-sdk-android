package com.qualifio.sample.ktx

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.qualifio.sample.R
import com.qualifio.sample.ui.navigation.Route

// Class names are obfuscated by R8, so titles come from resources.
@StringRes
fun NavKey.titleRes(): Int =
    when (this) {
        Route.Configuration -> R.string.navigation_configuration
        Route.Home -> R.string.navigation_home
        Route.Settings -> R.string.navigation_settings
        Route.SecureTokenFields -> R.string.navigation_secure_token_fields
        else -> R.string.app_name
    }
