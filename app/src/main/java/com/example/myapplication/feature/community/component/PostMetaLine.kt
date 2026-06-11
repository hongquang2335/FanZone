package com.example.myapplication.feature.community.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.theme.SoftText

@Composable
fun PostMetaLine(
    timeLabel: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    author: String? = null,
    onAuthorClick: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!author.isNullOrBlank()) {
            Text(
                text = author,
                color = SoftText,
                style = textStyle,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (onAuthorClick != null) Modifier.clickable(onClick = onAuthorClick) else Modifier
            )
            Text("·", color = SoftText, style = textStyle)
        }
        Text(
            text = timeLabel,
            color = SoftText,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
