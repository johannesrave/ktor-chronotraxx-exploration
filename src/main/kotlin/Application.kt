import auth.setupAuthentication
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.*
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
    val db = PostgresConfig.database

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, TimeFrames)
    }

    val accounts = PostgresUserAccountRepository
    val employees = PostgresEmployeeRepository
    val timeframes = PostgresTimeFrameRepository

    setupAuthentication(accounts)
    authenticationRouting(accounts)
    dashboardRouting(employees, timeframes)
    dashboardRouting(employees, timeframes)

    println("*** BOOTED UP ***")
}
