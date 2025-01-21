package net.nimbus.commons.entities;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.nimbus.commons.Commons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@ToString
public class Rank extends Entity<String> {

    private String color;

    private String tabPrefix;

    private String chatPrefix;

    private int tabWeight;

    private List<Rank> inheritances = new ArrayList<>();

    public Rank(String identifier) {
        super(identifier);
    }

    private List<Permission> getPermissions() {
        return Commons.getInstance().getPermissionService().filterCache(perm -> perm.getRankName().equals(getId()));
    }

    public List<String> getAllPermissions() {
        List<String> allPermissions = getPermissions().stream().map(Permission::getPermission).collect(Collectors.toList());

        for (Rank inheritance : inheritances) {
            allPermissions.addAll(inheritance.getPermissions().stream().map(Permission::getPermission).toList());
        }

        return allPermissions;
    }
}
