package com.qualifio.sample.ui.screens.settings.secure_token_fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun SecureTokenFieldsCard(
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_security_field_add_24),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.settings_secure_token_fields_title),
                style = Typography.bodyLarge,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right_24),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
fun SecureTokenFieldsCardPreview() {
    SecureTokenFieldsCard {}
}
