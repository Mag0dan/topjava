package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id")
    int delete(@Param("id") int id);

    Optional<Meal> findByIdAndUser(int id, User user);

    @Transactional
    @Query("SELECT m FROM Meal m JOIN FETCH m.user WHERE m.id=:id and m.user.id=:userId")
    Optional<Meal> getWithUser(@Param("id") int id, @Param("userId") int userId);

    List<Meal> findAllByUser(User user, Sort sort);

    List<Meal> findAllByUserAndDateTimeGreaterThanEqualAndDateTimeLessThan(User user, LocalDateTime start, LocalDateTime end, Sort sort);
}
