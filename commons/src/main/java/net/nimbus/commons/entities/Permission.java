package net.nimbus.commons.entities;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString
public class Permission extends Entity<Long> {

    private String permission;

    private String rankName;
}
