package com.qualifio.sample.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qualifio.sample.R
import com.qualifio.sample.ui.theme.Typography

@Composable
fun ContentCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_news_24),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = Typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = content,
                )
            }
        }
    }
}

@Preview
@Composable
fun ContentCardPreview() {
    ContentCard(
        title = "Content",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam fermentum urna elit, non elementum justo convallis in. Etiam porta justo vulputate lacus fermentum, ut accumsan neque malesuada. Pellentesque sit amet ipsum suscipit, feugiat nisi at, venenatis ante. Donec sagittis non metus in tempus. Nam felis justo, tincidunt et accumsan eu, blandit id metus. Vestibulum luctus placerat dictum. Aliquam eget libero tempor, pharetra sem nec, varius purus. In hac habitasse platea dictumst. Suspendisse sagittis fermentum leo quis consequat. Fusce pulvinar finibus magna eget elementum. Pellentesque in lobortis nisi, quis pulvinar lectus. Donec ut nulla sed turpis faucibus convallis nec sed nunc. Cras placerat nisl at erat fringilla tincidunt. Etiam magna augue, ultricies vel pretium vel, feugiat et enim.",
    )
}
