package net.nimbus.commons.database.services;

import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.Commons;
import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.NimbusPlayer;
import net.nimbus.commons.entities.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class NimbusPlayerService extends OfflineService<String, NimbusPlayer> {

    @Override
    public Optional<NimbusPlayer> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);
        if (result.isEmpty()) return Optional.empty();

        Optional<NimbusPlayer> optional = Optional.ofNullable(buildPlayer(result.getFirstRow()));
        optional.ifPresent(player -> updateCache(player.getUuid(), player));
        return optional;
    }

    public NimbusPlayer buildPlayer(Row row) {
        return NimbusPlayer.builder()
                .name(row.getString("name"))
                .discordId(row.getString("discordid"))
                .firstJoin(row.getDate("firstjoin"))
                .uuid(row.getString("uuid"))
                .ipAddress(row.getString("ipaddress"))
                .password(row.getString("password"))
                .build();
    }

    @Override
    public Long insertInternal(NimbusPlayer player) {
        Rank playerRank = player.getActiveRank();
        String sql = "INSERT INTO players(uuid, discordid, firstjoin, ipaddress, name, password, rank_name) VALUES (?,?,?,?,?,?,?)";
        return sqlWrite(sql, player.getUuid(), player.getDiscordId(), player.getFirstJoin(), player.getIpAddress(), player.getName(), player.getPassword(), playerRank.getRankName());
    }

    @Override
    public void update(NimbusPlayer object) {
        updateCache(object.getUuid(), object);

        String sql = "UPDATE players SET discordid = ?, ipaddress = ?, name = ?, password = ?, rank_name = ? WHERE uuid = ?";
        sqlWrite(sql, object.getDiscordId(), object.getIpAddress(), object.getName(), object.getPassword(), object.getActiveRank().getRankName(), object.getUuid());
    }

    public boolean loadNimbusPlayer(String uuid) {
        if (get(uuid).isPresent()) return true;

        Optional<NimbusPlayer> optional = query("SELECT * FROM players WHERE uuid = ?", uuid);
        if (optional.isEmpty()) return false;

        Commons.getInstance().getRankUpdateService().queryList("SELECT * FROM player_rank_updates WHERE player_uuid = ?", uuid);
        Commons.getInstance().getPenaltyUpdateService().queryList("SELECT * FROM player_penalty_updates WHERE player_uuid = ?", uuid);

        log.info("{} has been successfully loaded", optional.get().getName());
        return true;
    }

    public String getPlayerName(String uuid) {
        if (get(uuid).isPresent()) return get(uuid).get().getName();
        return sqlQuery("SELECT name FROM players WHERE uuid = ?", uuid).getFirstRow().getString("name");
    }
}
