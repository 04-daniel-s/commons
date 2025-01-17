package net.nimbus.commons.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.nimbus.commons.rank.RankStatus;

import java.util.Date;

@Data
@SuperBuilder
@ToString
public class RankUpdate extends Entity<Long> {

    private String playerUUID;

    private String rankName;

    private RankStatus rankStatus;

    private Date timestamp;

    private Date expireDate;

    private String executorUUID;

}
