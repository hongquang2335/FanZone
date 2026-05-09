package com.example.myapplication.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.core.designsystem.component.GradientPanel
import com.example.myapplication.core.designsystem.component.SectionHeader

@Composable
fun CommunityScreen(
    posts: List<CommunityPost>,
    onOpenEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                GradientPanel(
                    title = "Kham pha cong dong ngay.",
                    body = "Ket noi voi nhung nguoi ham mo cung dam me va khong bo lo bat ky su kien nao.",
                    action = "Tham gia ngay",
                    onAction = onOpenEvent
                )
            }
            if (isExpanded) {
                item { SectionHeader("Dong bai viet noi bat", "Co su kien kem tag") }
            }
            if (isExpanded) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        posts.chunked(2).forEach { group ->
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                group.forEach { post -> CommunityCard(post) }
                            }
                        }
                    }
                }
            } else {
                items(posts) { post -> CommunityCard(post) }
            }
        }
    }
}

