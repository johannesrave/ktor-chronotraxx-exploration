import auth.setupAuthentication
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.DbSettings
import persistence.PostgresUserAccountRepository
import persistence.TimeFrames
import persistence.Users
import java.io.File


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
//    val db = Database.connect(
//        url = "jdbc:postgresql://localhost:5434/chronotraxx_db",
//        driver = "org.postgresql.Driver",
//        user = "db_user",
//        password = "db_password"
//    )
    val db = DbSettings.database
//    db.config
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, TimeFrames)
    }

    transaction {
        addLogger(StdOutSqlLogger)
        Users.selectAll()
    }

    val accounts = PostgresUserAccountRepository
    setupAuthentication(accounts)

    println("BOOTIN'")

    routing {
        staticFiles("/", File("public"))
    }
}
