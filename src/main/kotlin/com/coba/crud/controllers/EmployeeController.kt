package com.coba.crud.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// 1. Data class automatically generates getters, setters, equals, hashCode, and toString
data class EmployeeRequest(val name: String, val email: String)
data class EmployeeResponse(val id: Long, val name: String, val email: String)

@RestController
@RequestMapping("/api/employees")
class EmployeeController {

    // 2. Short single-expression function syntax for GET requests
    @GetMapping
    fun getAllUsers(): List<EmployeeResponse> = listOf(
        EmployeeResponse(1, "Alice", "alice@example.com"),
        EmployeeResponse(2, "Bob", "bob@example.com")
    )

    // 3. Handling Path Variables and returning explicit ResponseEntities
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<EmployeeResponse> {
        val user = EmployeeResponse(id, "Alice", "alice@example.com")
        return ResponseEntity.ok(user)
    }

    // 4. Processing JSON payloads using @RequestBody
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: EmployeeRequest): EmployeeResponse {
        return EmployeeResponse(100, request.name, request.email)
    }
}
