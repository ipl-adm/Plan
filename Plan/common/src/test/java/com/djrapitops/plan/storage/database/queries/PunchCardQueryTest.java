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
package com.djrapitops.plan.storage.database.queries;

import com.djrapitops.plan.delivery.rendering.json.graphs.special.PunchCard;
import com.djrapitops.plan.delivery.rendering.json.graphs.special.SpecialGraphFactory;
import com.djrapitops.plan.storage.database.DatabaseTestPreparer;
import com.djrapitops.plan.storage.database.queries.analysis.QueryPunchCardQuery;
import com.djrapitops.plan.storage.database.queries.objects.SessionQueries;
import com.djrapitops.plan.storage.database.transactions.events.PlayerServerRegisterTransaction;
import com.djrapitops.plan.storage.database.transactions.events.WorldNameStoreTransaction;
import org.junit.jupiter.api.Test;
import utilities.RandomData;
import utilities.TestConstants;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface PunchCardQueryTest extends DatabaseTestPreparer {

    default void savePunchCardSessions() {
        db().executeTransaction(new WorldNameStoreTransaction(serverUUID(), worlds[0]));
        db().executeTransaction(new WorldNameStoreTransaction(serverUUID(), worlds[1]));
        db().executeTransaction(new PlayerServerRegisterTransaction(playerUUID, RandomData::randomTime, TestConstants.PLAYER_ONE_NAME, serverUUID()));
        db().executeTransaction(new PlayerServerRegisterTransaction(player2UUID, RandomData::randomTime, TestConstants.PLAYER_TWO_NAME, serverUUID()));
        RandomData.randomSessions(serverUUID(), worlds, playerUUID, player2UUID).forEach(
                session -> execute(DataStoreQueries.storeSession(session))
        );
        RandomData.randomSessions(serverUUID(), worlds, playerUUID, player2UUID).forEach(
                session -> execute(DataStoreQueries.storeSession(session))
        );
    }

    @Test
    default void punchCardQueryProducesSameResultsAsJavaAlgorithm() {
        savePunchCardSessions();

        SpecialGraphFactory specialGraphFactory = new SpecialGraphFactory(config());
        List<PunchCard.Dot> expected = specialGraphFactory.punchCard(
                db().query(SessionQueries.fetchAllSessions())
        ).getDots().stream().filter(dot -> dot.getZ() != 0).sorted().collect(Collectors.toList());
        List<PunchCard.Dot> result = db().query(new QueryPunchCardQuery(
                config().getTimeZone(),
                Long.MIN_VALUE,
                Long.MAX_VALUE,
                Collections.singletonList(playerUUID))
        );
        Collections.sort(result);
        assertEquals(expected, result);
    }

}
