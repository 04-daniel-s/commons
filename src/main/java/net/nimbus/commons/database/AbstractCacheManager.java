package net.nimbus.commons.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.nimbus.commons.cache.Cache;
import net.nimbus.commons.entities.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public abstract class AbstractCacheManager<K, T extends Entity<K>> {

    private final Cache<K, T> cache;

    public void updateCache(K identifier, T entity){
        cache.update(identifier, entity);
    }

    public void remove(K primaryKey) {
        cache.remove(primaryKey);
    }

    public Optional<T> get(K primaryKey) {
        return Optional.ofNullable(cache.get(primaryKey));
    }

    public List<T> filterCache(Predicate<T> predicate) {
        return getCache().getAll().stream().filter(predicate).collect(Collectors.toList());
    }
}


