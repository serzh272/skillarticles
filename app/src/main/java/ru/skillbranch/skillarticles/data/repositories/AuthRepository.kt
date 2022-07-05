package ru.skillbranch.skillarticles.data.repositories

import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.PrefManager

interface IAuthRepository {
    fun login(login: String, password: String)
}

class AuthRepository(
    private val prefs: PrefManager = PrefManager(),
    private val api: NetworkDataHolder = NetworkDataHolder
) : IAuthRepository {
    override fun login(login: String, password: String) {
        val (user, token) = api.login(login, password)
        prefs.accessToken = token
        prefs.profile = user
    }
}