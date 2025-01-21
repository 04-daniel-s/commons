package net.nimbus.commons.database.services;

import net.nimbus.commons.Commons;
import net.nimbus.commons.database.AbstractRepository;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.RankUpdate;
import net.nimbus.commons.rank.RankStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.OptionalInt;

public class RankUpdateService extends AbstractRepository<Long, RankUpdate> {

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

    public RankUpdate buildEntity(Row row) {
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

    public RankUpdate extendRank(String executorUUID, RankUpdate activeRankUpdate, OptionalInt optionalDays) {
        activeRankUpdate.setRankStatus(RankStatus.REMOVED);
        update(activeRankUpdate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activeRankUpdate.getExpireDate());

        if (optionalDays.isPresent()) {
            calendar.add(Calendar.DAY_OF_YEAR, optionalDays.getAsInt());
        } else {
            calendar.add(Calendar.YEAR, 10);
        }

        RankUpdate newRankUpdate = RankUpdate.builder()
                .rankStatus(RankStatus.UPDATED)
                .rankName(activeRankUpdate.getRankName())
                .expireDate(calendar.getTime())
                .timestamp(new Date())
                .playerUUID(activeRankUpdate.getPlayerUUID())
                .executorUUID(executorUUID)
                .build();

        Commons.getInstance().getRankUpdateService().createNew(newRankUpdate);

        return newRankUpdate;
    }
}
