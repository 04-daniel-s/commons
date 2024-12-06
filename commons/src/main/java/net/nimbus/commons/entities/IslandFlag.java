package net.nimbus.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nimbus.commons.database.LongIdentifierEntity;
import net.nimbus.commons.entities.enums.IslandFlagEnum;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IslandFlag implements LongIdentifierEntity {

    private Long id;

    private IslandFlagEnum flagType;

    private boolean active = true;

    private Long islandId;
}
