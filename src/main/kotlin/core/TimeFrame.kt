package core


import java.time.LocalDateTime
import java.util.*

data class TimeFrame(
    val id: UUID? = null,
    val begin: LocalDateTime,
    val end: LocalDateTime,
    val userId: UUID,
)

interface TimeFrameRepository {
    fun create(timeFrame: TimeFrame): UUID

    fun deleteById(id: UUID): Boolean

    fun update(timeFrame: TimeFrame): Boolean
    fun findAllByEmployee(id: UUID): Collection<TimeFrame>
}