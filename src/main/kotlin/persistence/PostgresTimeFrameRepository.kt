package persistence

import core.TimeFrame
import core.TimeFrameRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

object PostgresTimeFrameRepository : TimeFrameRepository {

    override fun create(timeFrame: TimeFrame): UUID = transaction {
        TimeFrames.insertAndGetId {
            it[user] = timeFrame.userId
            it[begin] = timeFrame.begin
            it[end] = timeFrame.end
        }.value
    }

//    override fun fetchById(id: UUID): TimeFrame = transaction {
//        TimeFrames
//            .select { TimeFrames.id eq id }
//            .map { row -> mapToTimeFrame(row) }
//            .first()
//    }
//
//    override fun fetchAll(): Collection<TimeFrame> = transaction {
//        TimeFrames
//            .selectAll()
//            .map { row -> mapToTimeFrame(row) }
//    }

    override fun update(timeFrame: TimeFrame): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = TimeFrames.update({ TimeFrames.id eq timeFrame.id }) {
            it[begin] = timeFrame.begin
            it[end] = timeFrame.end
            it[user] = timeFrame.userId
        }
        rowsUpdated == rowsToUpdate
    }

    override fun findAllByEmployee(id: UUID): Collection<TimeFrame> = transaction {
        TimeFrames.select { TimeFrames.user eq id }.map { raw ->
            TimeFrame(
                raw[TimeFrames.id].value,
                raw[TimeFrames.begin],
                raw[TimeFrames.end],
                raw[TimeFrames.user].value
            )
        }
    }


    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = TimeFrames.deleteWhere { TimeFrames.id eq id }
        rowsToDelete == rowsDeleted
    }

//    override fun <ResultRow> mapToTimeFrame(raw: ResultRow): TimeFrame =
//        TimeFrame(raw[TimeFrames.id].value, raw[TimeFrames.begin], raw[TimeFrames.end], raw[TimeFrames.user].value)
}
