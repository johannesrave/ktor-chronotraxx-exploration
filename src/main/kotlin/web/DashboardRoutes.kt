package web

import auth.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import view.Renderer

fun Application.dashboardRouting() {
    routing {
        authenticate("auth-session") {
            get("/") {
                call.principal<UserSession>()
                    ?.let { (id, name) ->
                        println("UserSession found with UUID $id, redirecting to dashboard")
                        call.respondRedirect("/dashboard/$id")
                    } ?: run {
                    println("UserSession not found, redirecting to login")
                    call.respondRedirect("/login")
                }
            }

            get("/dashboard/{dashboardId}") {
                val session = call.principal<UserSession>()!!
                val id = call.parameters["dashboardId"]
                if (session.id.toString() != id) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                println("Employee is authorized for dashboardId $id, responding with dashboard")
                val html = Renderer.render(
                    "src/main/resources/pages/dashboard.jinja2",
                    hashMapOf("name" to session.name)
                )
                call.respondText(html, ContentType.Text.Html)
            }

            post("/timeframes/create"){

            }
        }
    }
}