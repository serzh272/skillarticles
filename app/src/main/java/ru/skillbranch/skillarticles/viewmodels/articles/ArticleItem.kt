package ru.skillbranch.skillarticles.viewmodels.articles

import java.util.*

data class ArticleItem(
    val id: String,
    val date: Date = Date(),
    val author: String,
    val authorAvatar: String,
    val title: String,
    val description: String,
    val poster: String,
    val categoryId: String,
    val category: String,
    val categoryIcon: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val readDuration: Int = 0,
    val isBookmark: Boolean = false
)
