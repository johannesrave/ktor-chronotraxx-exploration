package persistence

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table

object Users : UUIDTable() {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 50)
    val email = varchar("email", 50).nullable()
    val profile = reference("profile", Profiles)
    val pinboard = reference("pinboard", Pinboards).nullable()
}

object Profiles: UUIDTable() {
    var name = varchar("name", 50)
    var surname = varchar("surname", 50).nullable()
    var description = varchar("description", 600)
    var picture = varchar("picture", 50).nullable()
}

object Contacts: IntIdTable() {
    val firstUserId = reference("first_user_id", Users)
    val secondUserId = reference("second_user_id", Users)

//    override val primaryKey = PrimaryKey(firstUserId, secondUserId)
}

object ContactRequests: Table() {
    val senderUserId = reference("first_user_id", Users)
    val recipientUserId = reference("second_user_id", Users)

    override val primaryKey = PrimaryKey(senderUserId, recipientUserId, name="contact_rel")
}

object Pinboards: UUIDTable() {
    var author = reference("author", Users)
    var message = varchar("message", 600)
}

object Pictures: UUIDTable() {
    var url= varchar("url", 600)
    var width= integer("width")
    var height= integer("height")
}