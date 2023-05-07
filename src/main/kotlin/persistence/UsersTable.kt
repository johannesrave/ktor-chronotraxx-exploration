package persistence

import core.User
import core.UserRepository
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object UsersTable : UUIDTable(), UserRepository {
    val username = varchar("username", 64).uniqueIndex()
    val password = varchar("password", 64)
    val email = varchar("email", 64).nullable()
    val currentTimeFrame = reference("currentTimeFrame", TimeFramesTable).nullable()

    override fun create(user: User): UUID = transaction {
        UsersTable.insertAndGetId {
            it[username] = user.username
            it[password] = user.password
            it[email] = user.email
        }.value
    }

    override fun fetchById(id: UUID): User = transaction {
        UsersTable.select { UsersTable.id eq id }.map { User(id, it[username], it[password], it[email]) }.first()
    }

    override fun fetchAll(): Collection<User> = transaction {
        UsersTable.selectAll().map { User(UUID.fromString(id), it[username], it[password], it[email]) }
    }

    override fun update(user: User): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = UsersTable.update({ UsersTable.id eq user.id }) {
            it[username] = user.username
            it[password] = user.password
            it[email] = user.email
            it[currentTimeFrame] = user.currentTimeFrame
        }
        rowsUpdated == rowsToUpdate
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = UsersTable.deleteWhere { UsersTable.id eq id }
        rowsToDelete == rowsDeleted
    }
}