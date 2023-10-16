package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {

    private static final Comparator<Meal> SORT_ORDER = Comparator.comparing(Meal::getDateTime).reversed();
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        int i = 0;
        for (Meal meal : MealsUtil.meals) {
            this.save(meal, i++ < MealsUtil.meals.size() / 2 ? 1 : 2);
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        log.info("save {} {}", meal, userId);
        Map<Integer, Meal> userRepository = repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userRepository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return userRepository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete {} {}", id, userId);
        Map<Integer, Meal> userRepository = repository.get(userId);
        if (userRepository == null) {
            return false;
        }
        return userRepository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get {} {}", id, userId);
        Map<Integer, Meal> userRepository = repository.get(userId);
        if (userRepository == null) {
            return null;
        }
        return userRepository.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll {}", userId);
        return getAllFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllFiltered(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        log.info("getAllFiltered {} {} {}", startDate, endDate, userId);
        return getAllFilteredByPredicate(userId, meal -> DateTimeUtil.isBetweenDateTime(meal.getDateTime(), startDate, endDate));
    }

    private List<Meal> getAllFilteredByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userRepository = repository.get(userId);
        if (userRepository == null) {
            return Collections.emptyList();
        }
        return userRepository
                .values()
                .stream()
                .filter(filter)
                .sorted(SORT_ORDER)
                .collect(Collectors.toList());
    }
}

