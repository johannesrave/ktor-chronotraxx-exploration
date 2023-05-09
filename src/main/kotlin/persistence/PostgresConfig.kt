package persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object PostgresConfig {
    fun connect() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5434/chronotraxx_db",
            driver = "org.postgresql.Driver",
            user = "db_user",
            password = "db_password"
        )
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, TimeFrames)
        }
    }
}
