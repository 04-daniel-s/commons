package net.nimbus.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nimbus.commons.ban.PenaltyReason;
import net.nimbus.commons.ban.PenaltyStatus;
import net.nimbus.commons.ban.PenaltyType;
import net.nimbus.commons.database.LongIdentifierEntity;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenaltyUpdate implements LongIdentifierEntity {

    private Long id;

    private String playerUUID;

    private Date timestamp;

    private Date expireDate;

    private PenaltyType type;

    private PenaltyReason penaltyReason;

    private PenaltyStatus penaltyStatus;

    private String reason;

    private String executor;

}
