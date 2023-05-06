package support

import support.KWatchEvent.Kind
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
val RefreshBrowserOnReload = createApplicationPlugin(name = "RefreshBrowserOnReload") {
    println("[refresh-on-reload server] installed")
    application.install(WebSockets)

    val connections = Collections.synchronizedSet<WebSocketConnection?>(LinkedHashSet())

    application.routing {
        webSocket("/alive") {
            val thisConnection = WebSocketConnection(this)
            connections += thisConnection

            println("[refresh-on-reload server] WS-connection established with client ${thisConnection.name}")
            thisConnection.session.send("[refresh-on-reload server] WS-connection established")
            for (frame in thisConnection.session.incoming) {
                send("[refresh-on-reload server] still alive")
            }
        }
    }

    application.launch {
        val watchRoot = File("build")
        val fileChannel = watchRoot.asWatchChannel(mode = KWatchChannel.Mode.Recursive)
        val initEvent = fileChannel.receive()

        if (initEvent.kind != Kind.Initialized)
            throw IllegalStateException("[refresh-on-reload server] Missing FileWatcher Initialized event")


        println("[refresh-on-reload server] watching files in " + fileChannel.file.name)
        while (true) {
            // receive any file-event from the coroutine that runs the fileWatcherService
            val fileEvent = fileChannel.receive()

            // flush fileEvents
            delay(1000L)
            for (event in fileChannel) {
                if (fileChannel.isEmpty || fileChannel.isClosedForReceive) break
            }

            println("[refresh-on-reload server] file-event received: ${fileEvent.kind} - closing WS-connections")
            for (connection in connections) {
                println("[refresh-on-reload server] closing session for ${connection.name}")
                connection.session.close()
            }
        }
    }
}