package ru.javawebinar.topjava.storage;

import java.util.List;

public interface Storage<K, V> {
    void clear();

    void save(V t);

    void update(V t);

    void delete(K k);

    V get(K k);

    int size();

    List<V> getAll();


}
