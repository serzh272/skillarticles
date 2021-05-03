package ru.skillbranch.skillarticles.data.adapters

import androidx.lifecycle.Transformations.map
import org.json.JSONObject
import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.extensions.asMap

class UserJsonAdapter() : JsonAdapter<User> {
    override fun fromJson(json: String): User? {
        val usr = User(id = "", name = "")
        val user = json.split(',').map {
            val keyValue = it.replace("[\'\"]".toRegex(), "").split(':')
            val key = keyValue.first()
            val value = keyValue.last()
            key to value
        }.toMap()
        return User(
            id = user["id"]!!,
            name = user["name"]!!,
            avatar = if (user["avatar"] == "null") null else user["avatar"],
            rating = user["rating"]?.toInt() ?: 0,
            respect = user["respect"]?.toInt() ?: 0,
            about = if (user["about"] == "null") null else user["about"]
        )
    }

    override fun toJson(obj: User?): String {
        val json =
            "\\((.*)\\)".toRegex().find(obj.toString())?.groupValues?.get(1)
                ?.replace(", ", ",")
                ?.replace("[^,=]+".toRegex()) {
                    "\"" + it.value + "\""
                }?.replace("=", ":")

        return json ?: ""
    }
}