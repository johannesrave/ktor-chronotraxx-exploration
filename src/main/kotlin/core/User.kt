package core

import kotlinx.datetime.Instant
import java.util.*

data class User(
    val id: UUID? = null,
    val username: String,
    val password: String,
    val email: String? = null,
    val currentTimeFrame: UUID? = null,
) {
    fun beginWorkingTimeFrame(instant: Instant) {
        throw NotImplementedError()
    }

    fun endWorkingTimeFrame(instant: Instant) {
        throw NotImplementedError()
    }

    fun getWorkingTimeFramesForDay() {
        throw NotImplementedError()
    }
}

interface UserRepository {
    fun create(user: User): UUID

    fun fetchById(id: UUID): User

    fun fetchAll(): Collection<User>

    fun deleteById(id: UUID): Boolean

    fun update(user: User): Boolean
}