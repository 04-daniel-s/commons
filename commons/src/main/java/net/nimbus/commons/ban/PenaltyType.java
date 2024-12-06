package net.nimbus.commons.ban;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PenaltyType {
    KICK("penalty.kick"),
    BAN("penalty.ban"),
    MUTE("penalty.mute"),
    UNBAN("penalty.unban"),
    UNMUTE("penalty.unmute"),;

    @Getter
    private final String permission;
}
