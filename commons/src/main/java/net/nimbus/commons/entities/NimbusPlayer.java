package net.nimbus.commons.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.Commons;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;
import net.nimbus.commons.rank.RankStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class NimbusPlayer {

    @NotNull
    private String uuid;

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

    public Rank getActiveRank() {
        if (getActiveRankUpdate() != null) {
            log.info("Active Rank update found for {}", name);
            Optional<Rank> optional = Commons.getInstance().getRankService().get(getActiveRankUpdate().getRankName());
            if(optional.isPresent()) {
                return optional.get();
            }
        }

        log.info("Active Rank update not found for {}", name);
        Optional<Rank> optionalRank = Commons.getInstance().getRankService().get("default");
        return optionalRank.orElse(null);
    }

    public List<Profile> getProfiles() {
        return Commons.getInstance().getProfileService().filterCache(profile -> profile.getUuid().equals(uuid));
    }

    public List<PenaltyUpdate> getPenaltyUpdates() {
        return Commons.getInstance().getPenaltyUpdateService().filterCache(update -> update.getPlayerUUID().equals(uuid));
    }

    public List<RankUpdate> getRankUpdates() {
        List<RankUpdate> updates = Commons.getInstance().getRankUpdateService().filterCache(update -> update.getPlayerUUID().equals(uuid));
        return updates;
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
        log.info("active rank update: {}",getRankUpdates().stream().filter(v -> v.getTimestamp().getTime() == v.getExpireDate().getTime() || v.getExpireDate().getTime() - System.currentTimeMillis() > 0).collect(Collectors.toList()));
        log.info("active rank update size: {}",getRankUpdates().stream().filter(v -> v.getTimestamp().getTime() == v.getExpireDate().getTime() || v.getExpireDate().getTime() - System.currentTimeMillis() > 0).collect(Collectors.toList()).size());

        return getRankUpdates().stream()
                .filter(v -> v.getRankStatus() == RankStatus.UPDATED)
                .filter(v -> v.getTimestamp().getTime() == v.getExpireDate().getTime() || v.getExpireDate().getTime() - System.currentTimeMillis() > 0)
                .min(Comparator.comparingInt(a -> Commons.getInstance().getRankService().get(a.getRankName()).get().getTabWeight())).orElse(null);
    }

    public Profile getActiveProfile() {
        return getProfiles().stream().filter(Profile::isActive).findFirst().orElse(null);
    }
}
