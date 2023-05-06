package support

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun Application.setupBrowserReload() {
    install(WebSockets)

    launch {
        val fileChannel = File("build/classes/kotlin/main")
            .asWatchChannel(mode = KWatchChannel.Mode.Recursive, scope = this)
        println("watching files in " + fileChannel.file.name)

        routing {
            val connections = Collections.synchronizedSet<WebSocketConnection?>(LinkedHashSet())
            webSocket("/alive") {
                val thisConnection = WebSocketConnection(this)
                connections += thisConnection

                coroutineScope {
                    launch {
                        val changedFile = fileChannel.receive()
                        println(LocalDateTime.now().toString() + "${changedFile.file.name} changed")
                        send("[live-reload] ${changedFile.file.name} changed")
                        thisConnection.session.close()
                    }
                }
                println("[live-reload] WS-connection established with client ${thisConnection.name}")
                thisConnection.session.send("[live-reload] WS-connection established")
                for (frame in thisConnection.session.incoming) {
                    send("[live-reload] still alive")
                }
            }
        }
    }
}

class WebSocketConnection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val name = "user${lastId.getAndIncrement()}"
}