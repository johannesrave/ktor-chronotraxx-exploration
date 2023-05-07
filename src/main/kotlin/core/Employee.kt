package core

import kotlinx.datetime.Instant
import java.util.*

data class Employee(
    val id: UUID,
    val username: String,
    val password: String,
    val email: String? = null,
    val currentTimeFrameId: UUID? = null,
) {
    fun beginWorkingTimeFrame(instant: Instant) {
        throw NotImplementedError()
    }

    fun endWorkingTimeFrame(instant: Instant) {
        throw NotImplementedError()
    }

    fun getWorkingTimeFramesForDay() {
        throw NotImplementedError()
    }
}

interface EmployeeRepository {

    fun fetchById(id: UUID): Employee

    fun fetchAll(): Collection<Employee>

    fun deleteById(id: UUID): Boolean

    fun update(employee: Employee): Boolean
}