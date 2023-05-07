package persistence

import core.TimeFrame
import core.TimeFrameRepository
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object TimeFrames : UUIDTable() {
    val begin = datetime("begin").defaultExpression(CurrentDateTime)
    val end = datetime("end").nullable()
    val user = reference("user", Users)

}

object PostgresTimeFramesRepository : TimeFrameRepository<ResultRow> {

    override fun create(timeFrame: TimeFrame): UUID = transaction {
        TimeFrames.insertAndGetId {
            it[user] = timeFrame.userId
        }.value
    }

    override fun fetchById(id: UUID): TimeFrame = transaction {
        TimeFrames
            .select { TimeFrames.id eq id }
            .map { row -> mapToTimeFrame(row) }
            .first()
    }

    override fun fetchAll(): Collection<TimeFrame> = transaction {
        TimeFrames
            .selectAll()
            .map { row -> mapToTimeFrame(row) }
    }

    override fun update(timeFrame: TimeFrame): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = TimeFrames.update({ TimeFrames.id eq timeFrame.id }) {
            it[begin] = timeFrame.begin
            it[end] = timeFrame.end
            it[user] = timeFrame.userId
        }
        rowsUpdated == rowsToUpdate
    }


    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = TimeFrames.deleteWhere { TimeFrames.id eq id }
        rowsToDelete == rowsDeleted
    }

    override fun mapToTimeFrame(raw: ResultRow): TimeFrame =
        TimeFrame(raw[TimeFrames.id].value, raw[TimeFrames.begin], raw[TimeFrames.end], raw[TimeFrames.user].value)
}
