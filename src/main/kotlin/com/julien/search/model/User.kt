package com.julien.search.model

import org.apache.commons.lang.StringUtils

data class User(
    val userName: String? = null,
    val email: String? = null,
    val adminUser: Boolean = false
) {
    fun isValid(): Boolean = (userName != null)

    object ModelMapper {
        fun from(dbMap: MutableMap<String, Any>): User =
            User(
                userName = StringUtils.trimToNull(dbMap["user_name"] as String?),
                email = StringUtils.trimToNull(dbMap["user_email"] as String?),
                adminUser = (dbMap["admin_user"] as Boolean?) ?: false
            )
    }
}