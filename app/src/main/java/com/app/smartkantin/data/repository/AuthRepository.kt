package com.app.smartkantin.data.repository

import com.app.smartkantin.data.dao.UserDao
import com.app.smartkantin.data.entity.UserEntity
import com.app.smartkantin.utils.Role

sealed class RegisterResult {
    object Success : RegisterResult()
    object EmailTaken : RegisterResult()
}

class AuthRepository(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: String,
        namaToko: String? = null
    ): RegisterResult {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return RegisterResult.EmailTaken
        }
        val newUser = UserEntity(
            nama = nama,
            email = email,
            password = password,
            role = role,
            namaToko = namaToko
        )
        userDao.insertUser(newUser)
        return RegisterResult.Success
    }
}