package core

import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

data class Profile(
    var name: String = "",
    var surname: String = "",
    var description: String = "",
    var picture: Picture? = null
)

data class Pinboard(
    val entries: MutableList<PinboardEntry> = mutableListOf()
)

data class PinboardEntry(
    val id: UUID = UUID.randomUUID(),
    var author: User,
    var message: String,
    var created: Instant = Clock.System.now(),
)

data class Contacts(
    val list: MutableList<User> = mutableListOf(),
    val outgoingRequests: MutableList<ContactRequest> = mutableListOf(),
    val incomingRequests: MutableList<ContactRequest> = mutableListOf()
)

data class ContactRequest(
    val sender: User,
    val recipient: User,
    var created: Instant = Clock.System.now(),
)

data class Picture(
    val id: UUID? = null,
    val url: Url,
    val width: Int,
    val height: Int
)
