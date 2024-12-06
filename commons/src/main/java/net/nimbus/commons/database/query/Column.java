package net.nimbus.commons.database.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Column {
    private final String identifier;

    private final Object value;

}
