package net.nimbus.commons.database.services;


import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.IslandMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IslandMemberService extends OfflineService<Long, IslandMember> {

    @Override
    public Optional<IslandMember> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<IslandMember> optional = Optional.ofNullable(buildIslandMember(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(update.getId(), update));

        return optional;
    }

    public List<IslandMember> queryList(String query, Object... parameters) {
        List<IslandMember> islandMembers = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            IslandMember islandMember = buildIslandMember(row);
            islandMembers.add(islandMember);
            updateCache(islandMember.getId(), islandMember);
        }

        return islandMembers;
    }

    private IslandMember buildIslandMember(Row row) {
        return IslandMember.builder()
                .islandId(row.getLong("id"))
                .id(row.getLong("id"))
                .profileId(row.getLong("profile_id"))
                .build();
    }

    @Override
    public Long insertInternal(IslandMember i) {
        String sql = "INSERT INTO island_members(island_id, profile_id) VALUES (?,?)";
        Object[] objects = {i.getIslandId(), i, i.getProfileId()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(IslandMember object) {

    }
}