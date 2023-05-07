//package app.routing
//
//import auth.UserSession
//import core.profile.ProfileRepository
//import view.Renderer
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.http.content.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import java.util.*
//
//
//fun Application.profileRouting() {
//    routing {
//        authenticate("auth-session") {
//            get("/") {
//                call.principal<UserSession>()?.profileId?.let {
//                    println("UserSession found with profileId $it")
//                    call.respondRedirect("/profile/$it")
//                } ?: run {
//                    println("UserSession not found, redirecting")
//                    call.respondRedirect("/login")
//                }
//            }
//
//            get("/profile/{profileId}") {
//                val id = call.parameters["profileId"]
//                val profile = ProfileRepository.fetchById(UUID.fromString(id))
//                println(profile)
//
//                profile?.let {
//                    Renderer.render(
//                        "src/main/resources/pages/profile/index.jinja2",
//                        hashMapOf("name" to it.name)
//                    )
//                }?.let {
//                    call.respondText(it, ContentType.Text.Html)
//                } ?: call.respondText(profile?.id.toString(), ContentType.Text.Html)
//            }
//
//            get("/profile/edit") {
//                Renderer.render(
//                    "src/main/resources/pages/profile/edit.jinja2"
//                )?.let {
//                    call.respondText(it, ContentType.Text.Html)
//                }
//            }
//        }
//    }
//
//    routing {
//        static("/html") {
//            resources("html")
//        }
//
//        static("/scripts") {
//            resources("scripts")
//        }
//    }
//}
