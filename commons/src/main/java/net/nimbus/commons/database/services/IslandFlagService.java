package net.nimbus.commons.database.services;

import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.IslandFlag;
import net.nimbus.commons.entities.enums.IslandFlagEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IslandFlagService extends OfflineService<Long, IslandFlag> {

    @Override
    public Optional<IslandFlag> query(String query, Object... parameters) {
        return Optional.empty();
    }

    @Override
    public Long insertInternal(IslandFlag i) {
        String sql = "INSERT INTO island_flags(island_id, flag_type) VALUES(?,?)";
        Object[] objects = {i.getIslandId(), i.getFlagType().toString()};
        return sqlWrite(sql, objects);
    }

    public List<IslandFlag> queryList(String query, Object... parameters) {
        List<IslandFlag> islandFlags = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            IslandFlag islandFlag = buildFlag(row);
            islandFlags.add(islandFlag);
            updateCache(islandFlag.getId(), islandFlag);
        }

        return islandFlags;
    }

    @Override
    public void update(IslandFlag object) {

    }

    public IslandFlag buildFlag(Row row) {
        return IslandFlag.builder()
                .flagType(IslandFlagEnum.valueOf(row.getString("flag_type")))
                .active(row.getInteger("active") == 1)
                .islandId(row.getLong("island_id"))
                .id(row.getLong("id"))
                .build();
    }
}
