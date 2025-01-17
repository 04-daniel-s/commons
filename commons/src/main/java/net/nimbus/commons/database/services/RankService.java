package net.nimbus.commons.database.services;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.nimbus.commons.Commons;
import net.nimbus.commons.database.AbstractRepository;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Permission;
import net.nimbus.commons.entities.Rank;
import net.nimbus.commons.util.FutureCallbackAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RankService extends AbstractRepository<String, Rank> {

    public void loadRanks() {
        ListenableFuture<List<Rank>> listenableFuture = queryListAsynchronously("SELECT * FROM `ranks` ORDER BY tabweight DESC");

        Futures.addCallback(listenableFuture, new FutureCallbackAdapter<>(ranks -> {
            for (Rank rank : ranks) {
                List<String> inheritedRankNames = getInheritedRanks(rank.getId());
                List<Rank> inheritanceRanks = inheritedRankNames.stream().filter(name -> get(name).isPresent()).map(name -> get(name).get()).collect(Collectors.toList());
                rank.setInheritances(inheritanceRanks);

                Commons.getInstance().getPermissionService().queryList("SELECT * FROM permissions WHERE rank_name = ?", rank.getId());

                updateCache(rank.getId(), rank);
            }
        }), Commons.getInstance().getExecutorService());

    }

    public Rank buildEntity(Row row) {
        return Rank.builder()
                .hexColor(row.getString("hexcolor"))
                .id(row.getString("rank_name"))
                .prefix(row.getString("prefix"))
                .tabWeight(row.getInteger("tabweight"))
                .build();
    }


    public List<String> getInheritedRanks(String rankName) {
        List<String> ranksToCheck = new ArrayList<>(Collections.singleton(rankName));
        List<String> inheritedRanks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            for (String checkRank : ranksToCheck) {
                if (ranksToCheck.isEmpty()) return inheritedRanks;

                List<String> permissions = getPermissions(checkRank).stream().map(Permission::getPermission).collect(Collectors.toList());

                List<String> inheritedRankNames = permissions.stream().
                        filter(perm -> perm.contains("inheritance."))
                        .map(perm -> perm.replace("inheritance.", ""))
                        .collect(Collectors.toList());

                inheritedRanks.addAll(inheritedRankNames);
                ranksToCheck = inheritedRankNames;
            }
        }
        return inheritedRanks;
    }

    public List<Permission> getPermissions(String rankName) {
        return Commons.getInstance().getPermissionService().queryList("SELECT * FROM permissions WHERE rank_name = ?", rankName);
    }

    @Override
    public Long insertInternal(Rank r) {
        String sql = "INSERT INTO ranks(rank_name, hexcolor, prefix, tabweight) VALUES (?,?,?,?)";
        Object[] objects = {r.getId(), r.getHexColor(), r.getPrefix(), r.getTabWeight()};
        return sqlWrite(sql, objects);
    }

    @Override
    public void update(Rank object) {
        /*
        EMPTY, WE DON'T NEED TO UPDATE PERMISSIONS
         */
    }
}
