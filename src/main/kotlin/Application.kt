import auth.setupAuthentication
import app.routing.profileRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import java.io.File


fun main() {
    embeddedServer(
        Netty,
        port = 8080,
//        watchPaths = listOf("classes"),
        host = "0.0.0.0",
        module = Application::init
    ).start(wait = true)
}

fun Application.init() {
    setupAuthentication()
    profileRouting()

    println("yea")

    routing {
        staticFiles("/", File("public"))
    }
}
