package core

import java.time.LocalDateTime
import java.util.*

class TimeTrackingService(private val employees: EmployeeRepository, private val timeFrames: TimeFrameRepository) {
    fun beginWorking(employeeId: String) {
        val employee = employees.findById(UUID.fromString(employeeId))
        if (employee.isCurrentlyWorkingSince == null) {
            println("Registering starting time")
            employees.update(Employee(employee.id, employee.username, LocalDateTime.now()))
        }
    }

    fun stopWorking(employeeId: String) {
        val employee = employees.findById(UUID.fromString(employeeId))
        if (employee.isCurrentlyWorkingSince != null) {
            println("Creating and persisting timeframe for employee ${employee.id}")
            timeFrames.create(TimeFrame(null, employee.isCurrentlyWorkingSince!!, LocalDateTime.now(), employee.id))
            employees.update(Employee(employee.id, employee.username, null))
        }
    }

    fun fetchWorkingTimes(employeeId: String) = timeFrames.findAllByEmployee(UUID.fromString(employeeId))
}
