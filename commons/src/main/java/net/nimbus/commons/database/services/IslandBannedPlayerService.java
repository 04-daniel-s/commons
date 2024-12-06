package net.nimbus.commons.database.services;

import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.IslandBannedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IslandBannedPlayerService extends OfflineService<Long, IslandBannedPlayer> {

    @Override
    public Optional<IslandBannedPlayer> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<IslandBannedPlayer> optional = Optional.ofNullable(buildIslandBannedPlayer(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(update.getId(), update));

        return optional;
    }

    public List<IslandBannedPlayer> queryList(String query, Object... parameters) {
        List<IslandBannedPlayer> islandBannedPlayers = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            IslandBannedPlayer islandBannedPlayer = buildIslandBannedPlayer(row);
            islandBannedPlayers.add(islandBannedPlayer);
            updateCache(islandBannedPlayer.getId(), islandBannedPlayer);
        }

        return islandBannedPlayers;
    }

    private IslandBannedPlayer buildIslandBannedPlayer(Row row) {
        return IslandBannedPlayer.builder()
                .islandId(row.getLong("id"))
                .id(row.getLong("id"))
                .uuid(row.getString("player_uuid"))
                .build();
    }

    @Override
    public Long insertInternal(IslandBannedPlayer i) {
        String sql = "INSERT INTO island_banned_players(island_id, player_uuid) VALUES (?,?)";
        Object[] objects = {i.getIslandId(), i.getUuid()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(IslandBannedPlayer object) {

    }
}
