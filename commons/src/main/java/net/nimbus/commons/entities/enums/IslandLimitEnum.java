package net.nimbus.commons.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IslandLimitEnum {
    SIZE("Inselgröße"),
    NETHERPORTAL("Netherportal"),
    ENTITY("Entitäten"),
    REDSTONE("Anzahl an Redstone"),
    ISLAND_MEMBER("Anzahl an Inselmitgliedern");

    private final String name;

}
