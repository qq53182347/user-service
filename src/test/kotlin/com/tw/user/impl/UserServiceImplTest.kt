package com.tw.user.service.impl

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.whenever
import com.tw.user.dao.UserDao
import com.tw.user.dao.po.UserInfo
import com.tw.user.exception.DataNotFoundException
import com.tw.user.service.model.User
import com.tw.user.service.model.UserStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class UserServiceImplTest {

    @InjectMocks
    private lateinit var userServiceImpl: UserServiceImpl

    @Mock
    private lateinit var userDao: UserDao

    private val mockUserId = 1L

    private fun getMockUserInfo(): Optional<UserInfo> {
        val userInfo = UserInfo()
        userInfo.id = mockUserId
        userInfo.email = "email@tw.com"
        userInfo.name = "john"
        userInfo.phone = "138439884373"
        userInfo.createdAt = LocalDateTime.now()
        return Optional.of(userInfo)
    }

    private val mockUser = User(null, "john", "138439884373", "email@tw.com", null)

    private val noValuePresentMessage = "data not found"

    @Test
    internal fun `should return the user when the gain user exists`() {
        whenever(userDao.findByIdAndStatus(mockUserId,UserStatus.Available.value)).thenReturn(getMockUserInfo())

        val user = userServiceImpl.getUser(mockUserId)

        assertEquals(user.id, mockUserId)
    }

    @Test
    internal fun `should throw DataNotFoundException when get user not exists`() {
        whenever(userDao.findByIdAndStatus(mockUserId,UserStatus.Available.value)).thenReturn(Optional.empty())

        assertThrows<DataNotFoundException>(noValuePresentMessage) {
            userServiceImpl.getUser(mockUserId)
        }
    }

    @Test
    internal fun `should return the user when save user successful`() {
        val mockUserInfo = getMockUserInfo().get()
        whenever(userDao.save(any<UserInfo>())).thenReturn(mockUserInfo)

        val user = userServiceImpl.saveUser(mockUser)

        assertEquals(user.id, mockUserId)
    }

    @Test
    internal fun `should throw RuntimeException when save user failed`() {
        val message = "server error"
        whenever(userDao.save(getMockUserInfo().get())).thenThrow(RuntimeException(message))

        assertThrows<RuntimeException>(message) {
            userServiceImpl.saveUser(mockUser)
        }
    }

    @Test
    internal fun `should return the user when delete user existed`() {
        val mockUserInfo = getMockUserInfo()
        whenever(userDao.findById(mockUserId)).thenReturn(mockUserInfo)
        whenever(userDao.saveAndFlush(mockUserInfo.get())).thenReturn(getMockUserInfo().get())

        val user = userServiceImpl.deleteUser(mockUserId)

        assertEquals(user.id, mockUserId)
    }

    @Test
    internal fun `should throw NoSuchElementException when delete user not existed`() {
        whenever(userDao.findById(mockUserId)).thenReturn(Optional.empty())

        assertThrows<DataNotFoundException>(noValuePresentMessage) {
            userServiceImpl.deleteUser(mockUserId)
        }
    }

    @Test
    internal fun `should throw RuntimeException when delete user failed`() {
        val message = "server error"
        whenever(userDao.findById(mockUserId)).thenThrow(RuntimeException(message))

        assertThrows<RuntimeException>(message) {
            userServiceImpl.deleteUser(mockUserId)
        }
    }

    @Test
    internal fun `should return the user when update user successful`() {
        val mockUserInfo = getMockUserInfo()
        whenever(userDao.findById(mockUserId)).thenReturn(mockUserInfo)
        whenever(userDao.saveAndFlush(mockUserInfo.get())).thenReturn(getMockUserInfo().get())

        val user = userServiceImpl.updateUser(mockUserId,mockUser)

        assertEquals(user.id, mockUserId)
    }

    @Test
    internal fun `should throw RuntimeException when update user failed`() {
        val message = "server error"
        whenever(userDao.findById(mockUserId)).thenThrow(RuntimeException(message))

        assertThrows<RuntimeException>(message) {
            userServiceImpl.updateUser(mockUserId,mockUser)
        }
    }

    @Test
    internal fun `should throw NoSuchElementException when update user not existed`() {
        whenever(userDao.findById(mockUserId)).thenThrow(DataNotFoundException(noValuePresentMessage))

        assertThrows<DataNotFoundException>(noValuePresentMessage) {
            userServiceImpl.updateUser(mockUserId,mockUser)
        }
    }

}