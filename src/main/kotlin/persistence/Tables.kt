package persistence

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object TimeFrames : UUIDTable() {
    val begin = datetime("begin").defaultExpression(CurrentDateTime)
    val end = datetime("end")
    val user = reference("user", Users)

}

object Users : UUIDTable() {
    val username = varchar("username", 64).uniqueIndex()
    val password = varchar("password", 64)
    val email = varchar("email", 64).nullable()
    val isCurrentlyWorkingSince = datetime("isCurrentlyWorkingSince").nullable()
}