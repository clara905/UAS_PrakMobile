package com.app.smartkantin.data.repository

import com.app.smartkantin.data.dao.UserDao
import com.app.smartkantin.data.entity.UserEntity
import com.app.smartkantin.utils.Role

sealed class RegisterResult {
    object Success : RegisterResult()
    object UsernameTaken : RegisterResult()
}

class AuthRepository(private val userDao: UserDao) {

    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username, password)
    }

    suspend fun register(nama: String, username: String, password: String): RegisterResult {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) {
            return RegisterResult.UsernameTaken
        }
        val newUser = UserEntity(
            nama = nama,
            username = username,
            password = password,
            role = Role.PEMBELI
        )
        userDao.insertUser(newUser)
        return RegisterResult.Success
    }
}