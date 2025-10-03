package pt.isel.mapper

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import java.time.Instant

class InstantMapper : ColumnMapper<Instant> {
    @Throws(SQLException::class)
    override fun map(
        rs: ResultSet,
        columnNumber: Int,
        ctx: StatementContext,
    ): Instant = Instant.ofEpochSecond(rs.getLong(columnNumber))
}
