package net.nimbus.commons.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nimbus.commons.database.LongIdentifierEntity;
import net.nimbus.commons.entities.enums.IslandLimitEnum;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IslandLimit implements LongIdentifierEntity {

    private Long id;

    private IslandLimitEnum limitType;

    private Long islandId;

    private Long value;

    private Long level;
}
