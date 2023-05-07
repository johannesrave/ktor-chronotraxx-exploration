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

object TimeFramesTable : UUIDTable(), TimeFrameRepository<ResultRow> {
    private val begin = datetime("begin").defaultExpression(CurrentDateTime)
    private val end = datetime("end").nullable()
    private val user = reference("user", UsersTable)

    override fun create(timeFrame: TimeFrame): UUID = transaction {
        TimeFramesTable.insertAndGetId {
            it[user] = timeFrame.userId
        }.value
    }

    override fun fetchById(id: UUID): TimeFrame = transaction {
        TimeFramesTable
            .select { TimeFramesTable.id eq id }
            .map { row -> mapToTimeFrame(row) }
            .first()
    }

    override fun fetchAll(): Collection<TimeFrame> = transaction {
        TimeFramesTable
            .selectAll()
            .map { row -> mapToTimeFrame(row) }
    }

    override fun update(timeFrame: TimeFrame): Boolean = transaction {
        val rowsToUpdate = 1
        val rowsUpdated = TimeFramesTable.update({ TimeFramesTable.id eq timeFrame.id }) {
            it[begin] = timeFrame.begin
            it[end] = timeFrame.end
            it[user] = timeFrame.userId
        }
        rowsUpdated == rowsToUpdate
    }


    override fun deleteById(id: UUID): Boolean = transaction {
        val rowsToDelete = 1
        val rowsDeleted = TimeFramesTable.deleteWhere { TimeFramesTable.id eq id }
        rowsToDelete == rowsDeleted
    }

    override fun mapToTimeFrame(raw: ResultRow): TimeFrame =
        TimeFrame(raw[id].value, raw[begin], raw[end], raw[user].value)
}
