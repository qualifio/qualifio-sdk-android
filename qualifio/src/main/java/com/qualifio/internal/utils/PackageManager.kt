package com.qualifio.internal.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

internal fun PackageManager.applicationInfo(packageName: String, flags: Int): ApplicationInfo {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(flags.toLong()))
        }

        else -> getApplicationInfo(packageName, flags)
    }
}
