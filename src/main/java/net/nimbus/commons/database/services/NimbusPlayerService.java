package net.nimbus.commons.database.services;

import net.nimbus.commons.Commons;
import net.nimbus.commons.database.AbstractRepository;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.NimbusPlayer;
import net.nimbus.commons.entities.Rank;

import java.util.Optional;

public class NimbusPlayerService extends AbstractRepository<String, NimbusPlayer> {

    public NimbusPlayer buildEntity(Row row) {
        return NimbusPlayer.builder()
                .name(row.getString("name"))
                .discordId(row.getString("discordid"))
                .firstJoin(row.getDate("firstjoin"))
                .id(row.getString("uuid"))
                .ipAddress(row.getString("ipaddress"))
                .password(row.getString("password"))
                .currentRankName(row.getString("rank_name"))
                .build();
    }

    @Override
    public Long insertInternal(NimbusPlayer player) {
        Rank playerRank = player.getActiveRank();
        String sql = "INSERT INTO players(uuid, discordid, firstjoin, ipaddress, name, password, rank_name) VALUES (?,?,?,?,?,?,?)";
        return sqlWrite(sql, player.getId(), player.getDiscordId(), player.getFirstJoin(), player.getIpAddress(), player.getName(), player.getPassword(), playerRank.getId());
    }

    @Override
    public void update(NimbusPlayer object) {
        updateCache(object.getId(), object);

        String sql = "UPDATE players SET discordid = ?, ipaddress = ?, name = ?, password = ?, rank_name = ? WHERE uuid = ?";
        sqlWrite(sql, object.getDiscordId(), object.getIpAddress(), object.getName(), object.getPassword(), object.getActiveRank().getId(), object.getId());
    }

    public boolean loadNimbusPlayer(String uuid) {
        if (get(uuid).isPresent()) return true;

        Optional<NimbusPlayer> optional = query("SELECT * FROM players WHERE uuid = ?", uuid);
        if (optional.isEmpty()) return false;

        Commons.getInstance().getRankUpdateService().queryList("SELECT * FROM player_rank_updates WHERE player_uuid = ?", uuid);
        Commons.getInstance().getPenaltyUpdateService().queryList("SELECT * FROM player_penalty_updates WHERE player_uuid = ?", uuid);

        System.out.println(optional.get().getName() + " has been successfully loaded");
        return true;
    }

    public String getPlayerName(String uuid) {
        if (get(uuid).isPresent()) return get(uuid).get().getName();
        return sqlQuery("SELECT name FROM players WHERE uuid = ?", uuid).getFirstRow().getString("name");
    }
}
