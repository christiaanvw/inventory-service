package com.intergamma.inventory.api

import com.intergamma.inventory.exception.InventoryException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

val LOG = LoggerFactory.getLogger(InventoryExceptionHandler::class.java)

@ControllerAdvice
class InventoryExceptionHandler {

    @ExceptionHandler(InventoryException::class)
    fun handleInventoryException(exception: InventoryException): ResponseEntity<InventoryProblem> {
        LOG.warn(exception.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(InventoryProblem(exception.message!!))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<InventoryProblem> {
        LOG.warn(exception.message)
        val error = InventoryProblem(
                description = "${exception.fieldError?.field ?: "unknown field"}: ${exception.fieldError?.defaultMessage ?: "validation error"}"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    class InventoryProblem(
        val description: String
    )
}