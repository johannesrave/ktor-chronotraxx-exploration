package web

import auth.UserAccountRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File
import java.util.*

data class UserSession(val id: UUID, val name: String) : Principal

fun Application.authenticationRouting(accounts: UserAccountRepository) {
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
