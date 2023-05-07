package persistence

import auth.UserAccount
import auth.UserAccountRepository
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.Users.email
import persistence.Users.id
import persistence.Users.password
import persistence.Users.username
import java.util.*

object PostgresUserAccountRepository : UserAccountRepository {
    override fun register(username: String, password: String, email: String): UserAccount = transaction {
        Users.insertAndGetId {
            it[Users.username] = username
            it[Users.password] = password
            it[Users.email] = email
        }.let { id -> UserAccount(id.value, username, password, email) }
    }

    override fun fetchById(id: UUID): UserAccount = transaction {
        Users.select { Users.id eq id }
            .map { UserAccount(id, it[username], it[password], it[email]) }
            .single()
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = Users.deleteWhere { Users.id eq id }
        rowsToDelete == rowsDeleted
    }

    override fun fetchByCredentials(credentials: UserPasswordCredential): UserAccount? =
        Users.select { (username eq credentials.name) and (password eq credentials.password) }
            .map { UserAccount(it[id].value, it[username], it[password], it[email]) }
            .singleOrNull()
}