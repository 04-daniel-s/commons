package net.nimbus.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rank {

    private String rankName;

    private String prefix;

    private String hexColor;

    private int tabWeight;

    private List<Rank> inheritances = new ArrayList<>();

    private List<Permission> permissions = new ArrayList<>();

    public List<String> getAllPermissions() {
        List<String> allPermissions = new ArrayList<>(permissions.stream().map(Permission::getPermission).collect(Collectors.toList()));

        for (Rank inheritance : inheritances) {
            allPermissions.addAll(inheritance.getAllPermissions());
        }

        System.out.println(rankName + " : " + allPermissions);
        return allPermissions;
    }
}
