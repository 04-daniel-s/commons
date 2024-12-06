package net.nimbus.commons.database.services;

import net.nimbus.commons.Commons;
import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Island;
import net.nimbus.commons.entities.IslandMember;
import net.nimbus.commons.entities.Profile;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IslandService extends OfflineService<Long, Island> {

    public Island buildIsland(Row row) {
        return Island.builder()
                .timestamp(row.getDate("timestamp"))
                .id(row.getLong("id"))
                .founderId(row.getLong("founder_id"))
                .serializedLocation(row.getString("serialized_location"))
                .serializedSpawnLocation(row.getString("serialized_spawnlocation"))
                .build();
    }

    public boolean loadIsland(Profile profile) {
        Optional<Island> optionalIsland = Commons.getInstance().getIslandService().query("SELECT * FROM islands WHERE founder_id = ?", profile.getId());
        if (optionalIsland.isEmpty()) return false;

        Long islandId = optionalIsland.get().getId();

        Commons.getInstance().getIslandBannedPlayerService().queryList("SELECT * FROM island_banned_players WHERE island_id = ?", islandId);
        Commons.getInstance().getIslandLimitService().queryList("SELECT * FROM island_limits WHERE island_id = ?", islandId);
        Commons.getInstance().getIslandFlagService().queryList("SELECT * FROM island_flags WHERE island_id = ?", islandId);

        List<IslandMember> members = Commons.getInstance().getIslandMemberService().queryList("SELECT * FROM island_members WHERE island_id = ?", islandId);
        members.stream().map(IslandMember::getProfileId).forEach(id -> Commons.getInstance().getProfileService().query("SELECT * FROM profiles WHERE id = ?", id));
        return true;
    }

    public Optional<Island> getFounderOf(Profile profile) {
        return filterCache(island -> island.getFounderId().longValue() == profile.getId().longValue()).stream().findFirst();
    }

    @Override
    public Optional<Island> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        if (result.isEmpty()) return Optional.empty();
        Optional<Island> optional = Optional.ofNullable(buildIsland(result.getFirstRow()));
        optional.ifPresent(island -> updateCache(island.getId(), island));

        return optional;
    }

    @Override
    public Long insertInternal(Island i) {
        String sql = "INSERT INTO islands(founder_id, serialized_location, serialized_spawnlocation, timestamp) VALUES (?,?,?,?)";
        Object[] objects = {i.getFounderId(), i.getSerializedLocation(), i.getSerializedSpawnLocation(), i.getTimestamp()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(Island object) {

    }
}
