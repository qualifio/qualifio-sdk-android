package com.qualifio.sample.ui.navigation

import com.qualifio.sample.R

internal data class BottomNavItem(
    val icon: Int,
    val label: Int,
)

internal val TOP_LEVEL_DESTINATIONS =
    mapOf(
        Route.Home to
            BottomNavItem(
                icon = R.drawable.ic_home_24,
                label = R.string.navigation_home,
            ),
        Route.Settings to
            BottomNavItem(
                icon = R.drawable.ic_settings_24,
                label = R.string.navigation_settings,
            ),
    )
