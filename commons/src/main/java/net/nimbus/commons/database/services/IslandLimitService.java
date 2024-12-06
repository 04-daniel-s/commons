package net.nimbus.commons.database.services;

import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.IslandLimit;
import net.nimbus.commons.entities.enums.IslandLimitEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IslandLimitService extends OfflineService<Long, IslandLimit> {

    @Override
    public Optional<IslandLimit> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<IslandLimit> optional = Optional.ofNullable(buildLimit(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(update.getId(), update));

        return optional;
    }

    public List<IslandLimit> queryList(String query, Object... parameters) {
        List<IslandLimit> islandLimits = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            IslandLimit islandLimit = buildLimit(row);
            islandLimits.add(islandLimit);
            updateCache(islandLimit.getId(), islandLimit);
        }

        return islandLimits;
    }

    @Override
    public Long insertInternal(IslandLimit i) {
        String sql = "INSERT INTO island_limits(island_id, limit_type, value, level) VALUES(?,?,?,?)";
        Object[] objects = {i.getIslandId(), i.getLimitType().toString(), i.getValue(), i.getLevel()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(IslandLimit object) {

    }

    public IslandLimit buildLimit(Row row) {
        return IslandLimit.builder()
                .limitType(IslandLimitEnum.valueOf(row.getString("limit_type")))
                .islandId(row.getLong("island_id"))
                .id(row.getLong("id"))
                .value(row.getLong("value"))
                .level(row.getLong("level"))
                .build();
    }
}
