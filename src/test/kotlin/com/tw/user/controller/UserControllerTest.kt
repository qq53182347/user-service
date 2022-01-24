package com.tw.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.tw.user.controller.dto.UserRequest
import com.tw.user.exception.DataNotFoundException
import com.tw.user.service.model.User
import com.tw.user.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
internal class UserControllerTest @Autowired constructor(val mockMvc: MockMvc, val objectMapper: ObjectMapper) {

    @MockBean
    private lateinit var userService: UserService

    private val mockUserId = 1L

    private val mockUser = User(
        mockUserId,
        "zhangSan",
        "18328393484",
        "email@tw.com",
        LocalDateTime.now()
    )

    private val mockUserRequest = UserRequest(
        "zhangSan",
        "18328393484",
        "email@tw.com"
    )

    private val noValuePresentMessage = "No value present"

    @Test
    internal fun `should return 200 with get user when user exists`() {
        whenever(userService.getUser(mockUserId)).thenReturn(mockUser)

        val performGet = mockMvc.get("/v1/users/$mockUserId")

        performGet.andExpect {
            status { isOk }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(mockUserId) }
        }
    }

    @Test
    internal fun `should return 404 without user when user does not exist`() {
        whenever(userService.getUser(mockUserId)).thenThrow(DataNotFoundException(noValuePresentMessage))

        val performGet = mockMvc.get("/v1/users/$mockUserId")

        performGet.andExpect {
            status { isNotFound }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.errors[0].message") { value(noValuePresentMessage) }
        }
    }

    @Test
    internal fun `should return 200 with user when user saved successful`() {
        whenever(userService.saveUser(any())).thenReturn(mockUser)

        val performPatch = mockMvc.post("/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mockUserRequest)
        }

        performPatch
            .andExpect {
                status { isCreated }
                content { contentType(MediaType.APPLICATION_JSON_VALUE) }
                jsonPath("$.name") { value(mockUser.name) }
                jsonPath("$.id") { isNotEmpty() }
            }
    }

    @Test
    internal fun `should return 500 when user save failed`() {
        val message = "server error"
        whenever(userService.saveUser(any())).thenThrow(RuntimeException(message))

        val performPatch = mockMvc.post("/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mockUserRequest)
        }

        performPatch.andExpect {
            status { is5xxServerError }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.errors[0].message") { value(message) }
        }
    }

    @Test
    internal fun `should return 200 with delete user when user exists`() {
        whenever(userService.deleteUser(mockUserId)).thenReturn(mockUser)

        val performGet = mockMvc.delete("/v1/users/$mockUserId")

        performGet.andExpect {status { isNoContent } }
    }

    @Test
    internal fun `should return 404 without user when delete user does not exist`() {
        whenever(userService.deleteUser(mockUserId)).thenThrow(DataNotFoundException(noValuePresentMessage))

        val performGet = mockMvc.delete("/v1/users/$mockUserId")

        performGet.andExpect {
            status { isNotFound }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.errors[0].message") { value(noValuePresentMessage) }
        }
    }

    @Test
    internal fun `should return 500 when user delete failed`() {
        val message = "server error"
        whenever(userService.deleteUser(any())).thenThrow(RuntimeException(message))

        val performGet = mockMvc.delete("/v1/users/$mockUserId")

        performGet.andExpect {
            status { is5xxServerError }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.errors[0].message") { value(message) }
        }
    }


    @Test
    internal fun `should return 200 with user when user update successful`() {
        whenever(userService.updateUser(any(),any())).thenReturn(mockUser)

        val performPut = mockMvc.perform(
            put("/v1/users/$mockUserId").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUserRequest))
            )

         performPut.andExpect(ResultMatcher.matchAll(MockMvcResultMatchers.status().isNoContent))
    }

    @Test
    internal fun `should return 500 when user update failed`() {
        val message = "server error"
        whenever(userService.updateUser(any(), any())).thenThrow(RuntimeException(message))

        val performPut = mockMvc.perform(
            put("/v1/users/$mockUserId").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUserRequest))
        )

        performPut.andExpect(ResultMatcher.matchAll(MockMvcResultMatchers.status().is5xxServerError))
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value(message))
    }

    @Test
    internal fun `should return 404 when update user does not exist`() {

        whenever(userService.updateUser(any(), any())).thenThrow(DataNotFoundException(noValuePresentMessage))

        val performPut = mockMvc.perform(
            put("/v1/users/$mockUserId").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUserRequest))
        )

        performPut.andExpect(ResultMatcher.matchAll(MockMvcResultMatchers.status().isNotFound))
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value(noValuePresentMessage))
    }

}