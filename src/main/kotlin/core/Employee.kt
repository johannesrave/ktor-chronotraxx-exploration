package core

import java.time.LocalDateTime
import java.util.*

data class Employee(
    val id: UUID,
    val username: String,
    var isCurrentlyWorkingSince: LocalDateTime? = null,
)

interface EmployeeRepository {

    fun findById(id: UUID): Employee

    fun findAll(): Collection<Employee>

    fun update(employee: Employee): Boolean
}