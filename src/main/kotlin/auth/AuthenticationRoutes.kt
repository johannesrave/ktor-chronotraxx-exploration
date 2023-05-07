package auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File
import java.util.*

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

    routing {
        route("/login") {
            get {
                call.sessions.get<UserSession>()?.also {
                    call.respondRedirect("/")
                } ?: kotlin.run {
                    call.respondFile(File("src/main/resources/html/login.html"))
                }
            }

            authenticate("auth-form-login") {
                post {
                    call.respondRedirect("/")
                }
            }
        }
    }
    routing {
        route("/register") {
            get {
                val userSession = call.sessions.get<UserSession>()
                if (userSession != null)
                    call.respondRedirect("/")
                else
                    call.respondFile(File("src/main/resources/html/register.html"))
            }

            post {
                val formParams = call.receiveParameters()
                val userName = formParams["username"]
                val userEmail = formParams["email"]
                val userPassword = formParams["password"]
                val userPasswordRepeat = formParams["password_repeat"]

                when {
                    userName.isNullOrBlank() || userEmail.isNullOrBlank()
                    -> call.respondRedirect("/register")

                    userPasswordRepeat != userPassword || userPassword.isNullOrBlank() || userPasswordRepeat.isNullOrBlank()
                    -> call.respondRedirect("/register")

                    else -> try {
                        accounts.register(userName, userPassword, userEmail)
                        call.response.header(HttpHeaders.Location, "/login")
                        call.response.status(HttpStatusCode.TemporaryRedirect)
                    } catch (error: Error) {
                        call.respondRedirect("/register")
                    }
                }
            }
        }
    }
}


data class UserSession(val id: UUID, val name: String) : Principal
