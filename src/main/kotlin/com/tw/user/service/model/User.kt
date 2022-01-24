package com.tw.user.service.model

import java.time.LocalDateTime

data class User(
    var id: Long?,
    val name: String,
    val phone: String,
    val email: String,
    var createdAt: LocalDateTime?
)
