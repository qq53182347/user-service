package com.tw.user.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java) as Logger

    @ExceptionHandler(value = [DataNotFoundException::class])
    @ResponseBody
    fun handleDataNotFoundException(e: DataNotFoundException): ResponseEntity<*> {
        logger.error("DataNotFoundException. errorMessage={}", e.message)
        return errorResponse(NOT_FOUND, null, e.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleThrowable(e: Throwable): ResponseEntity<*> {
        logger.error("Internal Server Error/General Exception occurred with message: {}", e.message)
        e.printStackTrace()
        return errorResponseWithoutParam(e.message, INTERNAL_SERVER_ERROR)
    }

    private fun errorResponse(status: HttpStatus, param: String?, message: String?): ResponseEntity<*> {
        val errors = listOf(ErrorData(param, message))
        return status(status).headers(getResponseHeaders()).body(ErrorMessage(errors))
    }

    private fun errorResponseWithoutParam(message: String?, status: HttpStatus): ResponseEntity<*> {
        val errors = listOf(ErrorData(null, message))
        return status(status).headers(getResponseHeaders()).body(ErrorMessage(errors))
    }

    private fun getResponseHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }
}

data class ErrorMessage(val errors: List<ErrorData>?)

data class ErrorData(val param: String?, val message: String?)
