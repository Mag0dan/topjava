package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private static final Sort SORT_DATE_TIME = Sort.by(Sort.Direction.DESC, "dateTime");

    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        User user = getUserById(userId);
        meal.setUser(user);
        if (meal.isNew()) {
            return crudRepository.save(meal);
        }
        return get(meal.id(), userId) == null ? null : crudRepository.save(meal);

    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = get(id, userId);
        return meal != null && crudRepository.delete(id) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        User user = getUserById(userId);
        return crudRepository.findByIdAndUser(id, user).orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        User user = getUserById(userId);
        return crudRepository.findAllByUser(user, SORT_DATE_TIME);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        User user = getUserById(userId);
        return crudRepository.findAllByUserAndDateTimeGreaterThanEqualAndDateTimeLessThan(user, startDateTime, endDateTime, SORT_DATE_TIME);
    }

    @Override
    public Meal getWithUser(int id, int userId) {
        return crudRepository.getWithUser(id, userId).orElse(null);
    }

    private User getUserById(int userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
