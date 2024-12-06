package net.nimbus.commons.entities;


import lombok.*;
import net.nimbus.commons.database.LongIdentifierEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Permission implements LongIdentifierEntity {

    private Long id;

    private String permission;

    private String rankName;
}
