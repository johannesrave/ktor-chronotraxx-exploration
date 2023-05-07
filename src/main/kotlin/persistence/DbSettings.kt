package persistence

import org.jetbrains.exposed.sql.Database

object DbSettings {
    val database by lazy {
        Database.connect(
            url = "jdbc:postgresql://localhost:5434/redpill_db",
            driver = "org.postgresql.Driver",
            user = "db_user",
            password = "db_password"
        )
    }
}
