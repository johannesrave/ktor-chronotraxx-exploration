package persistence

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class ProfileEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ProfileEntity>(Profiles)

    var name by Profiles.name
    var surname by Profiles.surname
    var description by Profiles.description
    var picture by PictureEntity optionalReferencedOn Profiles.picture
}

class ContactEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, ContactEntity>(Contacts)

    var firstUserId by Contacts.firstUserId
    var secondUserId by Contacts.secondUserId
}

class PinboardEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, PinboardEntity>(Pinboards)

    var author by Pinboards.author
    var message by Pinboards.message
}

class PictureEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, PictureEntity>(Pictures)

    var url by Pictures.url
    var width by Pictures.width
    var height by Pictures.height
}

