package com.tw.user.service.impl

import com.tw.user.dao.UserDao
import com.tw.user.dao.po.UserInfo
import com.tw.user.exception.DataNotFoundException
import com.tw.user.service.Constants.DATA_NOT_FOUND
import com.tw.user.service.model.User
import com.tw.user.service.UserService
import com.tw.user.service.model.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class UserServiceImpl(@Autowired val userDao: UserDao) : UserService {

    override fun getUser(id: Long): User {
        val selectedUser = userDao.findByIdAndStatus(id,UserStatus.Available.value)
        if (selectedUser.isEmpty) {
            throw DataNotFoundException(DATA_NOT_FOUND)
        }
        val gainUser =  selectedUser.get()
        return from(gainUser)
    }

    override fun saveUser(user: User): User {
        return from(userDao.save(from(user)))
    }

    override fun deleteUser(id: Long): User {
        val gainUser = findUser(id)
        gainUser.status = UserStatus.Deleted.value
        userDao.saveAndFlush(gainUser)
        return from(gainUser)
    }

    override fun updateUser(id: Long, from: User): User {
        val gainUser = findUser(id)
        gainUser.email=from.email
        gainUser.phone=from.phone
        gainUser.name=from.name
        return from(userDao.saveAndFlush(gainUser))
    }

    private fun findUser(id: Long): UserInfo {
        val selectedUser = userDao.findById(id)
        if (selectedUser.isEmpty) {
            throw DataNotFoundException(DATA_NOT_FOUND)
        }
        return selectedUser.get()
    }

    private fun from(userInfo: UserInfo): User {
        return User(userInfo.id, userInfo.name, userInfo.phone,
            userInfo.email, userInfo.createdAt)
    }

    private fun from(user: User): UserInfo {
        val userInfo = UserInfo()
        userInfo.id = null
        userInfo.name = user.name
        userInfo.phone = user.phone
        userInfo.email = user.email
        userInfo.status = UserStatus.Available.value
        userInfo.createdAt = LocalDateTime.now()
        return userInfo
    }
}
