package com.example.myapplication.domain.model

import androidx.annotation.DrawableRes

data class CommunityPost(
    val id: String,
    val author: String,
    val role: String,
    val topic: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    @param:DrawableRes val imageRes: Int?
)
