/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.storage.database.queries.analysis;

import com.djrapitops.plan.delivery.rendering.json.graphs.special.PunchCard;
import com.djrapitops.plan.storage.database.queries.QueryStatement;
import com.djrapitops.plan.storage.database.sql.tables.SessionsTable;
import org.apache.commons.text.TextStringBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.djrapitops.plan.storage.database.sql.building.Sql.*;

public class QueryPunchCardQuery extends QueryStatement<List<PunchCard.Dot>> {

    private final TimeZone timezone;
    private final long after;
    private final long before;

    public QueryPunchCardQuery(
            TimeZone timezone,
            long after,
            long before,
            List<UUID> playerUUIDs
    ) {
        super(createSQL(playerUUIDs));
        this.timezone = timezone;
        this.after = after;
        this.before = before;
    }

    private static String createSQL(List<UUID> playerUUIDs) {
        return SELECT +
                "FLOOR(" + SessionsTable.SESSION_START + "-?/?) * ? as time," +
                "COUNT(1) as sessions" +
                FROM + SessionsTable.TABLE_NAME +
                WHERE + SessionsTable.SESSION_START + "<=?" +
                AND + SessionsTable.SESSION_START + ">?" +
                AND + SessionsTable.USER_UUID + " IN ('" +
                new TextStringBuilder().appendWithSeparators(playerUUIDs, "','").build() +
                "')" +
                GROUP_BY + "time" +
                ORDER_BY + "time";
    }

    @Override
    public void prepare(PreparedStatement statement) throws SQLException {
        statement.setLong(1, timezone.getOffset(System.currentTimeMillis()));
        statement.setLong(2, TimeUnit.HOURS.toMillis(1L));
        statement.setLong(3, TimeUnit.HOURS.toMillis(1L));

        statement.setLong(4, before);
        statement.setLong(5, after);
    }

    @Override
    public List<PunchCard.Dot> processResults(ResultSet set) throws SQLException {
//        Calendar day = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
        Calendar day = Calendar.getInstance(timezone);

        int max = 1;
        List<PunchCard.Dot> dots = new ArrayList<>();
        while (set.next()) {
            long time = set.getLong("time");
            int sessionCount = set.getInt("sessions");

            day.setTimeInMillis(time);
            int hourOfDay = day.get(Calendar.HOUR_OF_DAY); // 0 AM is 0
            int dayOfWeek = day.get(Calendar.DAY_OF_WEEK) - 2; // Monday is 0, Sunday is -1
            if (dayOfWeek > 6) { // If Hour added a day on Sunday, move to Monday
                dayOfWeek = 0;
            }
            if (dayOfWeek < 0) { // Move Sunday to 6
                dayOfWeek = 6;
            }

            if (sessionCount > max) max = sessionCount;

            dots.add(new PunchCard.Dot(hourOfDay * 3600000, dayOfWeek, sessionCount, sessionCount));
        }

        for (PunchCard.Dot dot : dots) {
            dot.scaleBy(max);
        }
        return dots;
    }
}
