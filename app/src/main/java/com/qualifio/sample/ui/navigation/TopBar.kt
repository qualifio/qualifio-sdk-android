package com.qualifio.sample.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.qualifio.sample.R
import com.qualifio.sample.ktx.titleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentRoute: NavKey,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(currentRoute.titleRes())) },
        navigationIcon = {
            if (currentRoute !in TOP_LEVEL_DESTINATIONS.keys) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}
