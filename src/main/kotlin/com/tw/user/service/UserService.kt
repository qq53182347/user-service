package com.tw.user.service

import com.tw.user.service.model.User


interface UserService {
    fun getUser(id: Long): User
    fun saveUser(user: User): User
    fun deleteUser(id: Long): User
    fun updateUser(id: Long, from: User): User
}
