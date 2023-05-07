package auth

import io.ktor.server.auth.*
import java.util.*

data class UserAccount(
    val id: UUID,
    val name: String,
    val password: String,
    val email: String? = null
)

interface UserAccountRepository {
    fun register(username: String, password: String, email: String): UserAccount

    fun fetchById(id: UUID): UserAccount

    fun deleteById(id: UUID): Boolean

    fun fetchByCredentials(credentials: UserPasswordCredential): UserAccount?
}