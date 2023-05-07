package auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import java.util.*

data class UserSession(val id: UUID, val name: String) : Principal

fun Application.setupAuthentication(accounts: UserAccountRepository) {
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }

    install(Authentication) {
        form("auth-form-login") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                accounts.fetchByCredentials(credentials)?.let {account ->
                    val session = UserSession(name = account.name, id = account.id)
                    this.sessions.set(session)
                    session
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }

        session<UserSession>("auth-session") {
            validate { it }
            challenge { call.respondRedirect("/login") }
        }

        session<UserSession>("auth-session-repel") {
            validate { it }
            challenge { call.respondRedirect("/") }
        }
    }
}
