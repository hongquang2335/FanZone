package com.example.myapplication.feature.community

import android.net.Uri
import com.example.myapplication.domain.repository.SelectedCommunityMedia

data class CommunityPostUiState(
    val draft: String = "",
    val selectedMedia: List<SelectedCommunityMedia> = emptyList(),
    val anonymous: Boolean = false,
    val feeling: Boolean = false,
    val isPosting: Boolean = false,
    val currentAuthorId: String? = null,
    val currentAuthorName: String = "Bạn",
    val errorMessage: String? = null
) {
    val canPost: Boolean
        get() = !isPosting && (draft.isNotBlank() || selectedMedia.isNotEmpty())
}
