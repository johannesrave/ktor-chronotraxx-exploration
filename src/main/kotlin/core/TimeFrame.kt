package core


import java.time.LocalDateTime
import java.util.*

data class TimeFrame(
    val id: UUID? = null,
    val begin: LocalDateTime,
    val end: LocalDateTime?,
    val userId: UUID,
)

interface TimeFrameRepository<T> {
    fun create(timeFrame: TimeFrame): UUID

    fun fetchById(id: UUID): TimeFrame

    fun fetchAll(): Collection<TimeFrame>

    fun deleteById(id: UUID): Boolean

    fun update(timeFrame: TimeFrame): Boolean

    fun mapToTimeFrame(raw: T): TimeFrame
}