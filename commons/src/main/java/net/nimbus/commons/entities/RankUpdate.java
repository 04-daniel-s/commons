package net.nimbus.commons.entities;

import lombok.*;
import net.nimbus.commons.database.LongIdentifierEntity;
import net.nimbus.commons.rank.RankStatus;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RankUpdate implements LongIdentifierEntity {

    private Long id;

    private String playerUUID;

    private String rankName;

    private RankStatus rankStatus;

    private Date timestamp;

    private Date expireDate;

    private String executorUUID;

}
