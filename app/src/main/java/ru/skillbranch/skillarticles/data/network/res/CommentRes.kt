package ru.skillbranch.skillarticles.data.network.res

import ru.skillbranch.skillarticles.data.local.User
import java.util.*

data class CommentRes(
    val id: String,
    val user: User,
    val message: String,
    val date: Date,
    val slug: String,
    val answerTo: String? = null
)