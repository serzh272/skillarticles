package ru.skillbranch.skillarticles.extensions

import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.data.network.res.ArticleRes
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

fun ArticleState.toAppSettings(): AppSettings {
    return AppSettings(isDarkMode, isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}

fun ArticleState.asMap(): Map<String, Any?> = mapOf(
    "isAuth" to isAuth,
    "isLoadingContent" to isLoadingContent,
    "isLoadingReviews" to isLoadingReviews,
    "isLike" to isLike,
    "isBookmark" to isBookmark,
    "isShowMenu" to isShowMenu,
    "isBigText" to isBigText,
    "isDarkMode" to isDarkMode,
    "isSearch" to isSearch,
    "searchQuery" to searchQuery,
    "searchResults" to searchResults,
    "searchPosition" to searchPosition,
    "shareLink" to shareLink,
    "title" to title,
    "category" to category,
    "categoryIcon" to categoryIcon,
    "date" to date,
    "author" to author,
    "poster" to poster,
    "content" to content,
    "reviews" to reviews,
    "message" to message,
)

fun User.asMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "avatar" to avatar,
    "rating" to rating,
    "respect" to respect,
    "about" to about
)

fun ArticleRes.toArticleItem(): ArticleItem =
    ArticleItem(
        id = id,
        date = date,
        author = author,
        authorAvatar = authorAvatar,
        title = title,
        description = description,
        poster = poster,
        categoryId = category,
        category = category,
        categoryIcon = categoryIcon,
        likeCount = likeCount,
        commentCount = commentCount,
        readDuration = readDuration,
        isBookmark = false
    )

