package auth

import auth.AuthUserPostgresRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File
import java.util.*

val users = AuthUserPostgresRepository

fun Application.setupAuthentication() {
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
                getSessionBy(credentials)
                    ?.also {
                        println("logged in via form as ${it.name}")
                        this.sessions.set(it)
                    } ?: run {
                    println("couldn't log in as ${credentials.name} using password ${credentials.password}")
                    null
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
                println(call.response.status())
            }

            post {
                val formParams = call.receiveParameters()
                val userName = formParams["username"]
                val userPassword = formParams["password"]
                val userPasswordRepeat = formParams["password_repeat"]

                when {
                    userName.isNullOrBlank() ||
                    userPassword.isNullOrBlank() ||
                    userPasswordRepeat.isNullOrBlank() -> {
                        call.respondRedirect("/register")
                    }

                    userPasswordRepeat != userPassword -> {
                        call.respondRedirect("/register")
                    }

                    else -> {
                        try {
                            users.create(userName, userPassword)
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
}

private fun getSessionBy(credentials: UserPasswordCredential): UserSession? =
    users.fetchByCredentials(credentials)?.let {
        return UserSession(name = it.username, id = it.id, profileId = it.profileId)
    }

data class UserSession(val id: UUID, val name: String, val profileId: UUID) : Principal
