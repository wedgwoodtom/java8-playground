package com.tpatterson.playground.builder;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple builder example.
 */

public class MapBuilder<K, V>
{

}

/*
public class MapBuilder<K, V> //implements KeyBuilder<K, V>, ValueBuilder<K, V>
{
    private final List<Map.Entry<K, V>> entries;
    private K lastKey;

    public Maps()
    {
        this.entries = new ArrayList<>();
    }

    @Override
    public ValueBuilder<K, V> key(K k)
    {
        lastKey = k;
        return (ValueBuilder<K, V>) this;
    }

    @Override
    public KeyBuilder<K, V> value(V v)
    {
        entries.add(new AbstractMap.SimpleEntry<>(lastKey, v));
        return (KeyBuilder<K, V>) this;
    }

    @Override
    public Map<K, V> build()
    {
        return entries.stream().collect(toMap(Entry::getKey, Entry::getValue));
    }

    public static InitialKeyBuilder builder()
    {
        return new InitialKeyBuilder();
    }
    */

/*
@SuppressWarnings("unchecked")
protected static <K, V> Map<K, V> map(Object... args) {
    Map<K, V> res = new HashMap<>();
    K key = null;
    for (Object arg : args) {
        if (key == null) {
            key = (K) arg;
        } else {
            res.put(key, (V) arg);
            key = null;
        }
    }
    return res;
}
 */


/*
Guava:
Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

 */


/*
A simple map builder is trivial to write:

public class Maps {

    public static <Q,W> MapWrapper<Q,W> map(Q q, W w) {
        return new MapWrapper<Q, W>(q, w);
    }

    public static final class MapWrapper<Q,W> {
        private final HashMap<Q,W> map;
        public MapWrapper(Q q, W w) {
            map = new HashMap<Q, W>();
            map.put(q, w);
        }
        public MapWrapper<Q,W> map(Q q, W w) {
            map.put(q, w);
            return this;
        }
        public Map<Q,W> getMap() {
            return map;
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> map = Maps.map("one", 1).map("two", 2).map("three", 3).getMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
 */


