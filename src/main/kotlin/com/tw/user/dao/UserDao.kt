package com.tw.user.dao

import com.tw.user.dao.po.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserDao : JpaRepository<UserInfo, Long> {
    fun findByIdAndStatus( id:Long, status:Int) :Optional<UserInfo>
}
