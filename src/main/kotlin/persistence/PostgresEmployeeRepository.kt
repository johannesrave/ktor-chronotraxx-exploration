package persistence

import core.Employee
import core.EmployeeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*


object PostgresEmployeeRepository : EmployeeRepository {
    override fun findById(id: UUID): Employee = transaction {
        Users.select { Users.id eq id }
            .map { Employee(id, it[Users.username], it[Users.isCurrentlyWorkingSince]) }
            .first()
    }

    override fun findAll(): Collection<Employee> = transaction {
        Users.selectAll().map { Employee(UUID.fromString(id), it[Users.username]) }
    }

    override fun update(employee: Employee): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = Users.update({ Users.id eq employee.id }) {
            it[username] = employee.username
            it[isCurrentlyWorkingSince] = employee.isCurrentlyWorkingSince
        }
        rowsUpdated == rowsToUpdate
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = Users.deleteWhere { Users.id eq id }
        rowsToDelete == rowsDeleted
    }
}