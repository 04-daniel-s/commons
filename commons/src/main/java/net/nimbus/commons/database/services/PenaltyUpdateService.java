package net.nimbus.commons.database.services;

import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.ban.PenaltyReason;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;
import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.NimbusPlayer;
import net.nimbus.commons.entities.PenaltyUpdate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class PenaltyUpdateService extends OfflineService<Long, PenaltyUpdate> {

    @Override
    public Optional<PenaltyUpdate> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<PenaltyUpdate> optional = Optional.ofNullable(buildPenaltyUpdate(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(update.getId(), update));

        return optional;
    }

    public List<PenaltyUpdate> queryList(String query, Object... parameters) {
        List<PenaltyUpdate> penaltyUpdates = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            PenaltyUpdate penaltyUpdate = buildPenaltyUpdate(row);
            penaltyUpdates.add(penaltyUpdate);
            updateCache(penaltyUpdate.getId(), penaltyUpdate);
        }

        log.info("{} penalties have been queried", penaltyUpdates.size());
        return penaltyUpdates;
    }

    @Override
    public Long insertInternal(PenaltyUpdate p) {
        String sql = "INSERT INTO player_penalty_updates(expire_date, timestamp, reason, status, type, player_uuid, executor_uuid) VALUES (?,?,?,?,?,?,?)";
        Object[] objects = {p.getExpireDate(), p.getTimestamp(), p.getReason(), p.getPenaltyStatus().toString(), p.getType().toString(), p.getPlayerUUID(), p.getExecutor()};
        return sqlWrite(sql, objects);
    }


    public boolean isPunished(NimbusPlayer nimbusPlayer, PenaltyType type) {
        Optional<PenaltyUpdate> optional = nimbusPlayer.getActivePenalty(type);
        return optional.isPresent();
    }

    public Date calculateExpireDate(NimbusPlayer player) {
        List<PenaltyUpdate> penaltyUpdates = player.getPenaltyUpdates();
        Optional<Integer> optional = penaltyUpdates.stream().filter(p -> p.getPenaltyStatus() != PenaltyStatus.INADEQUATE)
                .map(p -> p.getPenaltyReason().getPoints()).reduce(Integer::sum);

        int points = optional.orElse(0);

        long milliseconds = System.currentTimeMillis() + 1000 * 60 * 60;

        for (int i = 0; i < points; i++) {
            milliseconds += (700) * (700) * (700);
        }

        if (milliseconds - System.currentTimeMillis() > 31_536_000_000L)
            return new Date(System.currentTimeMillis() * 2);
        return new Date(milliseconds);
    }

    @Override
    public void update(PenaltyUpdate object) {

    }

    private PenaltyUpdate buildPenaltyUpdate(Row row) {
        return PenaltyUpdate.builder()
                .id(row.getLong("id"))
                .executor(row.getString("executor_uuid"))
                .penaltyStatus(PenaltyStatus.valueOf(row.getString("status")))
                .penaltyReason(PenaltyReason.valueOf(row.getString("reason")))
                .playerUUID(row.getString("player_uuid"))
                .expireDate(row.getDate("expire_date"))
                .timestamp(row.getDate("timestamp"))
                .type(PenaltyType.valueOf(row.getString("type")))
                .build();
    }
}
