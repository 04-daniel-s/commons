package net.nimbus.commons.cache;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache<K, T> {

    @Getter
    private final Map<K, T> map;

    public Cache() {
        map = new HashMap<>();
    }

    public void addAll(Map<K, T> entities) {
        map.putAll(entities);
    }

    public T get(Object id) {
        if (!map.containsKey(id)) return null;
        return map.get(id);
    }

    public void update(K key, T object) {
        map.put(key, object);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    public void clear() {
        map.clear();
    }
}
