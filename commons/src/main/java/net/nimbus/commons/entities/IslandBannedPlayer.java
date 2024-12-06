package net.nimbus.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.nimbus.commons.database.LongIdentifierEntity;

@AllArgsConstructor
@Builder
@Data
public class IslandBannedPlayer implements LongIdentifierEntity {

    private Long id;

    private String uuid;

    private Long islandId;
}
