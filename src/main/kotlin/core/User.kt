package core

import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

class User(
    val id: UUID? = null,
    val username: String,
    val password: String,
    val email: String? = null
) {
    val pinboard: Pinboard = Pinboard()
    val contacts: Contacts = Contacts() // this should be moved to profile

    @OptIn(ExperimentalTime::class)
    fun beginWorkingTimeFrame(timeSource: TimeSource) {
        throw NotImplementedError()
    }


    @OptIn(ExperimentalTime::class)
    fun endWorkingTimeFrame(timeSource: TimeSource) {
        throw NotImplementedError()
    }


    fun getWorkingTimeFramesForDay() {
        throw NotImplementedError()
    }

    fun writeEntryToPinboardOf(otherUser: User, message: String): UUID {
        val entry = PinboardEntry(author = this, message = message)
        otherUser.pinboard.entries.add(entry)
        return entry.id
    }

    fun deleteEntryFromPinboardOf(otherUser: User, entryId: UUID): Boolean {
        return otherUser.pinboard.entries.removeIf { it.id == entryId }
    }

    fun requestBeingContactsWith(otherUser: User): ContactRequest {
        val request = ContactRequest(this, otherUser)
        this.contacts.outgoingRequests.add(request)
        otherUser.contacts.incomingRequests.add(request)
        return request
    }

    fun acceptBeingContactsWith(otherUser: User): Boolean {
        return this.contacts.incomingRequests
            .find { it.sender == otherUser }
            ?.let {
                this.contacts.incomingRequests.remove(it)
                otherUser.contacts.outgoingRequests.remove(it)
                this.contacts.list.add(otherUser)
                otherUser.contacts.list.add(this)
                true
            } ?: run { false }
    }
}

interface UserRepository {
    fun fetchById(id: UUID): User
    fun fetchAll(): Collection<User>


}