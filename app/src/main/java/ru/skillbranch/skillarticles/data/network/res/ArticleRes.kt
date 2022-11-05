package ru.skillbranch.skillarticles.data.network.res

import java.util.*

data class ArticleRes(
    val id: String,
    val date: Date,
    val author: String,
    val authorAvatar: String,
    val title: String,
    val description: String,
    val poster: String,
    val category: String,
    val categoryIcon: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val readDuration: Int = 0
)