package com.tw.user.controller

import com.tw.user.controller.dto.UserDto
import com.tw.user.controller.dto.UserRequest
import com.tw.user.service.model.User
import com.tw.user.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
@RequestMapping("/v1/users")
@Api("User Service API")
class UserController(private val userService: UserService) {

    @ApiOperation(value = "Get user by user id.", httpMethod = "GET")
    @ApiResponses(value = [ApiResponse(code = 200, message = "successful response")])
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUser(
        @PathVariable id: Long
    ): ResponseEntity<EntityModel<UserDto>>{
        val user = userService.getUser(id)
        return ResponseEntity.ok(EntityModel.of(from(user))
            .add(linkTo<UserController> { getUser(id) }.withSelfRel())
           .add(linkTo<UserController> { saveUser(UserRequest("","","")) }
                .withRel("save").withName(HttpMethod.POST.name),
            ))
    }

    @ApiOperation(value = "Delete user by user id.", httpMethod = "DELETE")
    @ApiResponses(value = [ApiResponse(code = 204, message = "")])
    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        userService.deleteUser(id)
    }

    @ApiOperation(value = "Save user by user id.", httpMethod = "POST")
    @ApiResponses(value = [ApiResponse(code = 201, message = "successful response")])
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveUser(@RequestBody @Valid userRequest: UserRequest):ResponseEntity<EntityModel<UserDto>> {
        val saveUser = userService.saveUser(from(userRequest))
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(from(saveUser))
            .add(linkTo<UserController> { getUser(saveUser.id!!) }.withSelfRel())
            .add(linkTo<UserController> { saveUser(userRequest) }
                    .withRel("update").withName(HttpMethod.PUT.name)
            )
        )
    }


    @ApiOperation(value = "Update user based on user id.", httpMethod = "PUT")
    @ApiResponses(value = [ApiResponse(code = 204, message = "")])
    @PutMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody @Valid userRequest: UserRequest
    ) {
       userService.updateUser(id,from(userRequest))
    }

    private fun from(userRequest: UserRequest): User {
        return User(null, userRequest.name, userRequest.phone, userRequest.email, null)
    }

    private fun from(user: User): UserDto {
        return UserDto(user.id, user.name, user.phone, user.email, user.createdAt)
    }
}
