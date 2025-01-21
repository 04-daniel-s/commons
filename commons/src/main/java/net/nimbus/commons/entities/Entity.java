package net.nimbus.commons.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString
@SuperBuilder
@AllArgsConstructor
public abstract class Entity<T> {

    private T id;
}
