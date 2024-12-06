package net.nimbus.commons.entities;

import lombok.*;
import net.nimbus.commons.Commons;
import net.nimbus.commons.database.LongIdentifierEntity;
import net.nimbus.commons.entities.enums.IslandFlagEnum;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Island implements LongIdentifierEntity {

    private Long id;

    private Long founderId;

    private Date timestamp;

    private String serializedLocation;

    private String serializedSpawnLocation;

    public List<IslandMember> getMembers() {
        return Commons.getInstance().getIslandMemberService().filterCache(member -> member.getIslandId().longValue() == id.longValue());
    }

    public List<IslandLimit> getLimits() {
        return Commons.getInstance().getIslandLimitService().filterCache(limit -> limit.getIslandId().longValue() == id.longValue());
    }

    public List<IslandFlag> getFlags() {
        return Commons.getInstance().getIslandFlagService().filterCache(flag -> flag.getIslandId().longValue() == id.longValue());
    }

    public boolean hasMember(Profile profile) {
        return getMembers().stream().anyMatch(p -> p.getId().longValue() == profile.getId().longValue());
    }

    public boolean hasFlag(IslandFlagEnum islandFlagEnum) {
        return getFlags().stream().anyMatch(flag -> flag.getFlagType() == islandFlagEnum);
    }
}
