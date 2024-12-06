package net.nimbus.commons.database.services;

import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.Commons;
import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.RankUpdate;
import net.nimbus.commons.rank.RankStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class RankUpdateService extends OfflineService<Long, RankUpdate> {

    @Override
    public Optional<RankUpdate> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<RankUpdate> optional = Optional.ofNullable(buildRankUpdate(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(update.getId(), update));

        return optional;
    }

    public List<RankUpdate> queryList(String query, Object... parameters) {
        List<RankUpdate> rankUpdates = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            RankUpdate rankUpdate = buildRankUpdate(row);
            rankUpdates.add(rankUpdate);
            updateCache(rankUpdate.getId(), rankUpdate);
        }

        log.info("{} rankUpdates have been queried", rankUpdates.size());
        return rankUpdates;
    }

    @Override
    public Long insertInternal(RankUpdate r) {
        String sql = "INSERT INTO player_rank_updates(player_uuid, executor_uuid, expire_date, timestamp, status, rank_name) VALUES (?,?,?,?,?,?)";
        Object[] objects = {r.getPlayerUUID(), r.getExecutorUUID(), r.getExpireDate(), r.getTimestamp(), r.getRankStatus().toString(), r.getRankName()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(RankUpdate object) {
        updateCache(object.getId(), object);
        String sql = "UPDATE player_rank_updates SET status = ? WHERE id = ?";

        Object[] objects = {object.getRankStatus().toString(), object.getId()};
        sqlWrite(sql, objects);
    }

    private RankUpdate buildRankUpdate(Row row) {
        return RankUpdate.builder()
                .id(row.getLong("id"))
                .executorUUID(row.getString("executor_uuid"))
                .rankName(row.getString("rank_name"))
                .rankStatus(RankStatus.valueOf(row.getString("status")))
                .playerUUID(row.getString("player_uuid"))
                .expireDate(row.getDate("expire_date"))
                .timestamp(row.getDate("timestamp"))
                .build();
    }

    public RankUpdate extendRank(String executorUUID, RankUpdate activeRankUpdate, Long time) {
        activeRankUpdate.setRankStatus(RankStatus.REMOVED);
        update(activeRankUpdate);

        RankUpdate newRankUpdate = RankUpdate.builder()
                .rankStatus(RankStatus.UPDATED)
                .rankName(activeRankUpdate.getRankName())
                .expireDate(new Date(activeRankUpdate.getExpireDate().getTime() + time))
                .timestamp(new Date())
                .playerUUID(activeRankUpdate.getPlayerUUID())
                .executorUUID(executorUUID)
                .build();

        Commons.getInstance().getRankUpdateService().createNew(newRankUpdate);

        return newRankUpdate;
    }
}
