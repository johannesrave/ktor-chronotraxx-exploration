package web

import auth.UserSession
import core.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import view.Renderer

fun Application.dashboardRouting(
    timeFrames: TimeFrameRepository, timeTrackingService: TimeTrackingService
) {
    routing {
        authenticate("auth-session") {
            get("/") {
                call.principal<UserSession>()?.let { (id, name) ->
                        println("UserSession found with UUID $id, redirecting to dashboard")
                        call.respondRedirect("/dashboard/$id")
                    } ?: run {
                    println("UserSession not found, redirecting to login")
                    call.respondRedirect("/login")
                }
            }

            route("/dashboard/{dashboardId}"){
                get {
                    val session = call.principal<UserSession>()!!
                    val employeeId = call.parameters["dashboardId"]
                    if (session.id.toString() != employeeId) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val html = Renderer.render(
                        "src/main/resources/pages/dashboard.jinja2",
                        hashMapOf("name" to session.name, "timeframes" to timeTrackingService.fetchWorkingTimes(employeeId))
                    )
                    call.respondText(html, ContentType.Text.Html)
                }

                post {
                    val session = call.principal<UserSession>()!!
                    val employeeId = call.parameters["dashboardId"]!!
                    if (session.id.toString() != employeeId) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val workingAction = call.receiveParameters()["working"]

                    when (workingAction) {
                        "start" -> timeTrackingService.beginWorking(employeeId)
                        "stop" -> timeTrackingService.stopWorking(employeeId)
                        else -> throw IllegalStateException("Employee is working already or hasn't begun yet")
                    }

                    call.respondRedirect("/dashboard/${employeeId}")
                }
            }
        }
    }
}

