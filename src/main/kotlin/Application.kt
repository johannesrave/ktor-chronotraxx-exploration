import auth.setupAuthentication
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.PostgresSettings
import persistence.PostgresUserAccountRepository
import persistence.TimeFrames
import persistence.Users
import web.authenticationRouting
import web.dashboardRouting


fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::init
    ).start(wait = true)
}

fun Application.init() {
    this.install(CallLogging)
    val db = PostgresSettings.database

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, TimeFrames)
    }

    val accounts = PostgresUserAccountRepository

    setupAuthentication(accounts)
    authenticationRouting(accounts)
    dashboardRouting()

    println("*** BOOTED UP ***")
}
