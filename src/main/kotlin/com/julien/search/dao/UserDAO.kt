package com.julien.search.dao

import com.julien.search.model.User

interface UserDAO {
    fun validateUserName(userId: Int): User
}
