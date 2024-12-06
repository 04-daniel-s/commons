package net.nimbus.commons.database.services;

import net.nimbus.commons.Commons;
import net.nimbus.commons.database.OfflineService;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Permission;
import net.nimbus.commons.entities.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RankService extends OfflineService<String, Rank> {

    public void loadRanks() {
        getAll(ranks -> {
            ranks.forEach(rank -> {
                List<Permission> permissions = getPermissions(rank.getRankName());
                rank.setPermissions(permissions);
                updateCache(rank.getRankName(), rank);
            });

            List<Rank> updatedRanks = getCache().getAll();
            updatedRanks.forEach(rank -> {
                List<String> rankNames = getInheritances(rank.getRankName());
                List<Rank> inheritanceRanks = rankNames.stream().map(name -> get(name).get()).collect(Collectors.toList());
                rank.setInheritances(inheritanceRanks);
                updateCache(rank.getRankName(), rank);
            });
        });
    }

    public void getAll(Consumer<List<Rank>> consumer) {
        Commons.getInstance().getExecutorService().execute(() -> {
            String query = "SELECT * FROM ranks";
            Result result = sqlQuery(query);
            consumer.accept(result.getRows().stream().map(row -> Commons.getInstance().getRankService().buildRank(row)).collect(Collectors.toList()));
        });
    }

    @Override
    public Optional<Rank> query(String query, Object... parameters) {
        Result rs = sqlQuery(query, parameters);
        return Optional.ofNullable(buildRank(rs.getFirstRow()));
    }

    public Rank buildRank(Row row) {
        if (row == null) return null;
        return Rank.builder()
                .hexColor(row.getString("hexcolor"))
                .rankName(row.getString("rank_name"))
                .prefix(row.getString("prefix"))
                .tabWeight(row.getInteger("tabweight"))
                .build();
    }

    public Permission buildPermission(Row row) {
        return Permission.builder()
                .id(row.getLong("id"))
                .permission(row.getString("permission"))
                .rankName(row.getString("rank_name"))
                .build();
    }

    public List<String> getInheritances(String rankName) {
        List<Rank> ranks = sqlQuery("SELECT * FROM ranks").getRows().stream().map(this::buildRank).collect(Collectors.toList());
        List<Rank> inheritances = new ArrayList<>();

        List<String> permissions = getPermissions(rankName).stream().map(Permission::getPermission).collect(Collectors.toList());
        List<String> inheritanceRankNames = permissions.stream()
                .filter(perm -> perm.contains("inheritance."))
                .map(perm -> perm.replace("inheritance.", ""))
                .collect(Collectors.toList());

        List<String> allRankNames = ranks.stream().map(Rank::getRankName).collect(Collectors.toList());

        for (String rank : allRankNames) {
            if (!inheritanceRankNames.contains(rank)) continue;
            inheritances.add(ranks.stream().filter(r -> r.getRankName().equals(rank)).findFirst().orElse(null));
        }

        return inheritances.stream().map(Rank::getRankName).collect(Collectors.toList());
    }

    public List<Permission> getPermissions(String rankName) {
        Result result = sqlQuery("SELECT * FROM permissions WHERE rank_name = ?", rankName);
        return result.getRows().stream().map(this::buildPermission).collect(Collectors.toList());
    }

    @Override
    public Long insertInternal(Rank r) {
        String sql = "INSERT INTO ranks(rank_name, hexcolor, prefix, tabweight) VALUES (?,?,?,?)";
        Object[] objects = {r.getRankName(), r.getHexColor(), r.getPrefix(), r.getTabWeight()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(Rank object) {

    }
}
