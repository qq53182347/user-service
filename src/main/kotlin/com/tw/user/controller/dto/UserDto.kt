package com.tw.user.controller.dto

import java.time.LocalDateTime

data class UserDto (
    var id: Long?,
    val name: String,
    val phone: String,
    val email: String,
    var createdAt: LocalDateTime?
 )
