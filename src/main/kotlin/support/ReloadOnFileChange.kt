package support

import io.ktor.server.application.*
import io.ktor.server.engine.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

val ReloadOnFileChange = createApplicationPlugin(name = "ReloadOnFileChange") {
//    Class.forName()
    println("[reload-on-change] installed")
    application.launch {
        val fileChannel = File("build/classes/kotlin/main").asWatchChannel(mode = KWatchChannel.Mode.Recursive)
        println("[reload-on-change] watching files in " + fileChannel.file.name)

        val initEvent = fileChannel.receive() // to remove the initializationEvent from the Channel
        println("[reload-on-change] init-event: ${initEvent.kind}")

        val jobs: MutableSet<Job> = mutableSetOf()
        for (fileEvent in fileChannel) {
            if (fileEvent.kind == KWatchEvent.Kind.Deleted) {
                // if a file has been deleted, it MAY HAVE BEEN a dynamically loaded .class file that is still in use.
                // we wait a little longer, hoping to find it in a second.
                delay(1000L)
            }
            println("[reload-on-change] detected file-change ")

            for (job in jobs) {
                println("[reload-on-change] cancelling $job")
                job.cancel()
            }

            try {
                val job = launch {
                    println("[reload-on-change] adding countdown for ${fileEvent.file}: ${fileEvent.kind}")
                    delay(300L)
                }
                jobs.add(job)
            } catch (e: Error) {
                // if a file has been deleted during the gradle build, it MAY HAVE BEEN the .class for
                // the countdown-coroutine which will make the classloader throw a java.lang.NoClassDefFoundError
                // when it tries to get the class at runtime.
                // the coroutine-class-file might not be recreated with the same name, so even polling for its
                // existence can fail. we therefore give up and just try to reload the application after a longish
                // grace-period of 2 seconds. this may STILL not be enough if the whole gradle build still takes
                // longer than that time and critical classes are only built later.
                // this sucks because it contains implicit knowledge about the gradle-build-process :/
                println(e)
                delay(2000L)
            } finally {
                println("[reload-on-change] ********************* reloading *********************")
                (application.environment as ApplicationEngineEnvironmentReloading).reload()
            }
        }
    }
}