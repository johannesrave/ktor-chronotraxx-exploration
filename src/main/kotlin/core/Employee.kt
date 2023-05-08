package core

import java.time.LocalDateTime
import java.util.*

data class Employee(
    val id: UUID,
    val username: String,
    var isCurrentlyWorkingSince: LocalDateTime? = null,
) {
//    val startOfCurrentTimeFrame: Query

    fun startWork(moment: LocalDateTime) {
        isCurrentlyWorkingSince = moment
    }

    fun endWork() {
        isCurrentlyWorkingSince = null
    }

    fun getWorkingTimeFramesForDay() {
        throw NotImplementedError()
    }

    fun isCurrentlyWorking() = isCurrentlyWorkingSince == null
}

interface EmployeeRepository {

    fun findById(id: UUID): Employee

    fun findAll(): Collection<Employee>

    fun deleteById(id: UUID): Boolean

    fun update(employee: Employee): Boolean
}