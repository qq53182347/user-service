package com.tw.user.controller.dto

import javax.validation.constraints.NotEmpty

data class UserRequest(
    @field:NotEmpty(message = "name not be null  or ''") val name: String,
    @field:NotEmpty(message = "phone not be null or ''") val phone: String,
    @field:NotEmpty(message = "email not be null or ''") val email: String
)
