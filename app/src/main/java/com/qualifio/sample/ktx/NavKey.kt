package com.qualifio.sample.ktx

import androidx.navigation3.runtime.NavKey

fun NavKey.displayName(): String =
    this::class
        .simpleName
        ?.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        ?: "App"
