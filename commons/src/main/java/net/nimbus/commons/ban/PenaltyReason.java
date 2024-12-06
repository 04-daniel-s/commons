package net.nimbus.commons.ban;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PenaltyReason {
    EXTREM(100, PenaltyType.BAN),
    HASSREDE(100, PenaltyType.MUTE),
    VERHALTEN(20, PenaltyType.BAN),
    SKIN(15, PenaltyType.BAN),
    CHATVERHALTEN(5, PenaltyType.MUTE);

    private final int points;

    private final PenaltyType type;
}
