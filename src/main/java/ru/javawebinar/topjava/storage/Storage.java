package ru.javawebinar.topjava.storage;

import java.util.List;

public interface Storage<K, V> {
    V save(V t);

    void delete(K k);

    V get(K k);

    List<V> getAll();
}
