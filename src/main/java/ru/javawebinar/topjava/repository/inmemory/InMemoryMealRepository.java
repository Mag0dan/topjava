package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<Meal> SORT_ORDER_DATETIME_DESC = Comparator.comparing(Meal::getDateTime).reversed();

    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> this.save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (!checkUserId(meal, userId)) {
            return null;
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        } else if (!checkUserId(repository.get(meal.getId()), userId)) {
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = repository.get(id);
        if (!checkUserId(meal, userId)) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return checkUserId(meal, userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values()
                .stream()
                .filter(meal -> checkUserId(meal, userId))
                .sorted(SORT_ORDER_DATETIME_DESC)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllFiltered(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        List<Meal> mealList = getAll(userId);
        mealList.removeIf(meal -> !DateTimeUtil.isBetweenDateTime(meal.getDateTime(), startDate, endDate));
        return mealList;
    }

    private boolean checkUserId(Meal meal, int userId) {
        return meal != null && meal.getUserId() == userId;
    }
}

