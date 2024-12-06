package net.nimbus.commons.database.services;

import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ProfileService extends OfflineService<Long, Profile> {
/*TODO Bei allen Services überprüfen, ob folgende Funktionen da sind:
    - cache
    - loadentity
    - deleteentity (vielleicht nicht überall)
    - createentity
 */
    @Override
    public Optional<Profile> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<Profile> optional = Optional.ofNullable(buildProfile(result.getFirstRow()));
        optional.ifPresent(profile -> updateCache(profile.getId(), profile));

        return optional;
    }

    public List<Profile> queryList(String query, Object... parameters) {
        List<Profile> profiles = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            Profile profile = buildProfile(row);
            profiles.add(profile);
            updateCache(profile.getId(), profile);
        }

        return profiles;
    }

    public Profile buildProfile(Row row) {
        return Profile.builder()
                .uuid(row.getString("player_uuid"))
                .id(row.getLong("id"))
                .money(row.getLong("money"))
                .inventory(row.getString("inventory"))
                .active(row.getBoolean("active"))
                .build();
    }

    @Override
    public Long insertInternal(Profile p) {
        String sql = "INSERT INTO profiles(player_uuid, inventory, money, active) VALUES (?,?,?,?)";
        Object[] objects = {p.getUuid(), p.getInventory(), p.getMoney(), p.isActive()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(Profile object) {

    }
}
