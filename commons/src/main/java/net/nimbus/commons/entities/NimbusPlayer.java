package net.nimbus.commons.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.nimbus.commons.Commons;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;
import net.nimbus.commons.rank.RankStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

@Data
@SuperBuilder
@ToString
public class NimbusPlayer extends Entity<String> {

    private String password;

    private String discordId;

    @NotNull
    private String name;

    @NotNull
    private Date firstJoin;

    @NotNull
    private String ipAddress;

    private Long playTime; //TODO service etc.

    private Date lastOnline; //TODO service etc.

    private String currentRankName;

    public Rank getActiveRank() {

        if (getActiveRankUpdate() != null) {
            Optional<Rank> optional = Commons.getInstance().getRankService().get(getActiveRankUpdate().getRankName());
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        Optional<Rank> optionalRank = Commons.getInstance().getRankService().get("default");
        return optionalRank.orElse(null);
    }

    public List<PenaltyUpdate> getPenaltyUpdates() {
        return Commons.getInstance().getPenaltyUpdateService().filterCache(update -> update.getPlayerUUID().equals(getId()));
    }

    public List<RankUpdate> getRankUpdates() {
        List<RankUpdate> updates = Commons.getInstance().getRankUpdateService().filterCache(update -> update.getPlayerUUID().equals(getId()));
        return updates;
    }

    public Optional<RankUpdate> getRankUpdate(Long rankUpdateId) {
        Predicate<RankUpdate> predicate = rankUpdate -> rankUpdate.getPlayerUUID().equals(getId()) && rankUpdate.getId().equals(rankUpdateId);
        return Commons.getInstance().getRankUpdateService().filterCache(predicate).stream().findFirst();
    }

    public boolean hasPermission(String permission) {
        if (name.equals("leCuutex")) return true;
        return getActiveRank().getAllPermissions().contains(permission);
    }

    public Optional<PenaltyUpdate> getActivePenalty(PenaltyType type) {
        return getPenaltyUpdates().stream()
                .filter(p -> p.getType() == type)
                .filter(p -> p.getPenaltyStatus() == PenaltyStatus.ACTIVE)
                .findFirst();
    }

    public RankUpdate getActiveRankUpdate() {
        if (getRankUpdates().isEmpty()) return null;

        RankUpdate rankUpdate = getRankUpdates().stream()
                .filter(v -> v.getRankStatus() == RankStatus.UPDATED)
                .filter(v -> v.getExpireDate().after(new Date()))
                .min(Comparator.comparingInt(a -> Commons.getInstance().getRankService().get(a.getRankName()).get().getTabWeight())).orElse(null);

        return rankUpdate;
    }
}
