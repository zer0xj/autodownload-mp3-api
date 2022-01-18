package com.julien.search.repository

import com.julien.search.model.User

interface UserRepository {
    fun selectUserByUserName(userId: Int): User
}
