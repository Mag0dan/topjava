package ru.javawebinar.topjava.repository.jdbc;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;
import static ru.javawebinar.topjava.util.DateTimeUtil.atStartOfDayOrMin;
import static ru.javawebinar.topjava.util.DateTimeUtil.atStartOfNextDayOrMax;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class JdbcMealRepositoryTest extends TestCase {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private JdbcMealRepository repository;

    @Test
    public void saveExist() {
        Meal meal = repository.save(userMeal, USER_ID);
        assertMatch(meal, userMeal);
    }

    @Test
    public void saveNew() {
        Meal meal = repository.save(getNew(), USER_ID);
        Meal newMeal = getNew();
        newMeal.setId(meal.getId());
        assertMatch(meal, newMeal);
    }

    @Test
    public void saveUpdate() {
        Meal updated = getUpdated();
        repository.save(updated, USER_ID);
        assertMatch(repository.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void saveWrongUser() {
        assertMatch(repository.save(getUpdated(), ADMIN_ID), null);
    }

    @Test
    public void delete() {
        boolean deleted = repository.delete(userMeal.getId(), USER_ID);
        Assert.assertTrue(deleted);

        assertMatch(repository.get(userMeal.getId(), USER_ID), null);
    }

    @Test
    public void deleteWrongUser() {
        boolean deleted = repository.delete(userMeal.getId(), ADMIN_ID);
        Assert.assertFalse(deleted);
    }

    @Test
    public void deleteNotFound() {
        boolean deleted = repository.delete(0, USER_ID);
        Assert.assertFalse(deleted);
    }

    @Test
    public void get() {
        assertMatch(repository.get(userMeal.getId(), USER_ID), userMeal);
    }

    @Test
    public void getWrongUser() {
        assertMatch(repository.get(userMeal.getId(), ADMIN_ID), null);
    }

    @Test
    public void getNotFound() {
        assertMatch(repository.get(0, USER_ID), null);
    }

    @Test
    public void getAll() {
        assertMatch(repository.getAll(USER_ID), userMeals);
    }

    @Test
    public void getAllNotFound() {
        assertMatch(repository.getAll(GUEST_ID), Collections.EMPTY_LIST);
    }

    @Test
    public void getBetweenHalfOpenAllRecords() {
        List<Meal> all = repository.getBetweenHalfOpen(atStartOfDayOrMin(null), atStartOfNextDayOrMax(null), USER_ID);
        assertMatch(all, userMeals);
    }

    @Test
    public void getBetweenHalfOpenPartRecords() {
        LocalDateTime startDateTime = LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0);

        List<Meal> all = repository.getBetweenHalfOpen(startDateTime, endDateTime, USER_ID);
        assertMatch(all, userMeals.subList(1, 4));
    }

    @Test
    public void getBetweenHalfOpenNoRecords() {
        LocalDateTime startDateTime = LocalDateTime.of(2021, Month.JANUARY, 31, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2021, Month.JANUARY, 31, 20, 0);

        List<Meal> all = repository.getBetweenHalfOpen(startDateTime, endDateTime, USER_ID);
        assertMatch(all, Collections.EMPTY_LIST);
    }
}