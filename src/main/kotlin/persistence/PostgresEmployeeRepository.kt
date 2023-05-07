package persistence

import core.Employee
import core.EmployeeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


object PostgresEmployeeRepository : EmployeeRepository {
    override fun fetchById(id: UUID): Employee = transaction {
        Users.select { Users.id eq id }.map { Employee(id, it[Users.username], it[Users.password], it[Users.email]) }.first()
    }

    override fun fetchAll(): Collection<Employee> = transaction {
        Users.selectAll().map { Employee(UUID.fromString(id), it[Users.username], it[Users.password], it[Users.email]) }
    }

    override fun update(employee: Employee): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = Users.update({ Users.id eq employee.id }) {
            it[username] = employee.username
            it[password] = employee.password
            it[email] = employee.email
            it[currentTimeFrame] = employee.currentTimeFrameId
        }
        rowsUpdated == rowsToUpdate
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = Users.deleteWhere { Users.id eq id }
        rowsToDelete == rowsDeleted
    }
}