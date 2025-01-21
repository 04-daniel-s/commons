package net.nimbus.commons.database.services;

import net.nimbus.commons.database.AbstractRepository;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Permission;

public class PermissionService extends AbstractRepository<Long, Permission> {

    @Override
    public Long insertInternal(Permission p) {
        String sql = "INSERT INTO permissions(rank_name,permission) VALUES (?,?)";
        Object[] objects = {p.getRankName(), p.getPermission()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(Permission object) {

    }

    public Permission buildEntity(Row row) {
        return Permission.builder().id(row.getLong("id")).permission(row.getString("permission")).rankName(row.getString("rank_name")).build();
    }

    public void removePermission(Permission permission) {
        remove(permission.getId());
        String sql = "DELETE FROM permissions WHERE id = ?";
        Object[] objects = {permission.getId()};
        sqlWrite(sql, objects);
    }
}
