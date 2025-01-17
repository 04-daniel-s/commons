package net.nimbus.commons.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.nimbus.commons.ban.PenaltyReason;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;

import java.util.Date;

@Data
@SuperBuilder
@ToString
public class PenaltyUpdate extends Entity<Long> {

    private String playerUUID;

    private Date timestamp;

    private Date expireDate;

    private PenaltyType type;

    private PenaltyReason penaltyReason;

    private PenaltyStatus penaltyStatus;

    private String reason;

    private String executor;

}
