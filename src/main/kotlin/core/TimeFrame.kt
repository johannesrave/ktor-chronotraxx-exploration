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

//    fun fetchById(id: UUID): TimeFrame

//    fun fetchAll(): Collection<TimeFrame>

    fun deleteById(id: UUID): Boolean

    fun update(timeFrame: TimeFrame): Boolean
    fun findAllByEmployee(id: UUID): Collection<TimeFrame>

//    fun <T> mapToTimeFrame(raw: T): TimeFrame
}