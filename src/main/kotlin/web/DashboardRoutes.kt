package web

import auth.UserSession
import core.Employee
import core.EmployeeRepository
import core.TimeFrame
import core.TimeFrameRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import view.Renderer
import java.time.LocalDateTime

fun Application.dashboardRouting(employees: EmployeeRepository, timeFrames: TimeFrameRepository) {
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
                val employeeTimeFrames = timeFrames.findAllByEmployee(session.id)
                val html = Renderer.render(
                    "src/main/resources/pages/dashboard.jinja2",
                    hashMapOf("name" to session.name, "timeframes" to employeeTimeFrames)
                )
                call.respondText(html, ContentType.Text.Html)
            }

            post("/dashboard/{dashboardId}") {
                val session = call.principal<UserSession>()!!
                val id = call.parameters["dashboardId"]
                if (session.id.toString() != id) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                println("Employee is authorized for dashboardId $id")
                val employee = employees.findById(session.id)
                val formParameters = call.receiveParameters()
                val working = formParameters["working"].toString()
                println(employee)
                println(working)

                if (working == "start" && employee.isCurrentlyWorkingSince == null) {
                    println("Registering starting time")
//                    employee.startWork(LocalDateTime.now())
                    employees.update(Employee(employee.id, employee.username, LocalDateTime.now()))
                } else if (working == "stop" && employee.isCurrentlyWorkingSince != null) {
                    println("Creating and persisting timeframe for employee $id")
                    timeFrames.create(TimeFrame(null, employee.isCurrentlyWorkingSince!!, LocalDateTime.now(), employee.id))
                    employees.update(Employee(employee.id, employee.username, null))
                }

                call.respondRedirect("/dashboard/${employee.id}")
            }
        }
    }
}