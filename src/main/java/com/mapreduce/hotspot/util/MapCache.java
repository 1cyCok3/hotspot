package com.mapreduce.hotspot.util;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache<K, V> {
    private PriorityQueue<Tuple<Long, K>> timeToLivePriorityQueue = new PriorityQueue<>(new Comparator<Tuple<Long, K>>() {
        @Override
        public int compare(Tuple<Long, K> o1, Tuple<Long, K> o2) {
            return o1.a.compareTo(o2.a);
        }
    });
    private int capacity = -1;
    private Map<K, V> map;

    public MapCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity of Cache must be positive number");
        }
        this.capacity = capacity;
        map = new ConcurrentHashMap<>();
    }

    public synchronized void put(K key, V value) {
        if (map.containsKey(key)) {
            map.put(key, value);
            return;
        }
        if (timeToLivePriorityQueue.size() >= capacity) {
            Tuple<Long, K> tuple = timeToLivePriorityQueue.poll();
            map.remove(tuple.b);
        }
        timeToLivePriorityQueue.add(new Tuple<>(System.currentTimeMillis(), key));
        map.put(key, value);
    }

    public synchronized V get(K key) {
        if (hit(key)) {
            return map.get(key);
        }
        return null;
    }

    public synchronized boolean hit(K key) {
        return map.containsKey(key);
    }
}
