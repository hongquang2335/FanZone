package com.example.myapplication.feature.community.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.myapplication.core.designsystem.theme.Evergreen

@Composable
fun ExpandableText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    textLimit: Int = 50
) {
    if (text.isBlank()) return

    var isExpanded by remember { mutableStateOf(false) }

    if (text.length > textLimit) {
        if (!isExpanded) {
            Text(
                text = buildAnnotatedString {
                    append(text.take(textLimit))
                    append("... ")
                    withStyle(style = SpanStyle(color = Evergreen, fontWeight = FontWeight.Bold)) {
                        append("Xem thêm")
                    }
                },
                style = style,
                modifier = Modifier.clickable { isExpanded = true }
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    append(text)
                    append(" ")
                    withStyle(style = SpanStyle(color = Evergreen, fontWeight = FontWeight.Bold)) {
                        append("Rút gọn")
                    }
                },
                style = style,
                modifier = Modifier.clickable { isExpanded = false }
            )
        }
    } else {
        Text(text, style = style)
    }
}
