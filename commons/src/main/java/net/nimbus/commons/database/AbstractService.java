package net.nimbus.commons.database;

import lombok.Getter;
import net.nimbus.commons.cache.Cache;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractService<K, T> {

    private final Cache<K, T> cache;

    public AbstractService(Cache<K, T> cache) {
        this.cache = cache;
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    }

    public void remove(K primaryKey) {
        cache.remove(primaryKey);
    }

    public void updateCache(K primaryKey, T entity) {
        cache.update(primaryKey, entity);
    }

    public abstract Optional<T> query(String query, Object... parameters);

    public Optional<T> get(K primaryKey) {
        return Optional.ofNullable(cache.get(primaryKey));
    }

    public List<T> filterCache(Predicate<T> predicate) {
        return getCache().getMap().values().stream().filter(predicate).collect(Collectors.toList());
    }
}


