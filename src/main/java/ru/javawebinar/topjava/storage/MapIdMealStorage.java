package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapIdMealStorage implements Storage<Integer, Meal> {
    private final ConcurrentHashMap<Integer, Meal> storage = new ConcurrentHashMap<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public void save(Meal meal) {
        meal.setId(nextId.getAndIncrement());
        storage.put(meal.getId(), meal);
    }

    @Override
    public void update(Meal meal) {
        storage.put(meal.getId(), meal);
    }

    @Override
    public void delete(Integer id) {
        storage.remove(id);
    }

    @Override
    public Meal get(Integer key) {
        return storage.get(key);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
