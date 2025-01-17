package net.nimbus.commons.database.services;

import net.nimbus.commons.ban.PenaltyReason;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;
import net.nimbus.commons.database.AbstractRepository;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.NimbusPlayer;
import net.nimbus.commons.entities.PenaltyUpdate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PenaltyUpdateService extends AbstractRepository<Long, PenaltyUpdate> {

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR,1);

        for (int i = 0; i < points; i++) {
            calendar.add(Calendar.DAY_OF_YEAR,7);
        }

        return calendar.getTime();
    }

    @Override
    public void update(PenaltyUpdate object) {

    }

    public PenaltyUpdate buildEntity(Row row) {
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
