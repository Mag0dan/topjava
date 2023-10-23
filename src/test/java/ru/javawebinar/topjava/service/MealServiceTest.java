package ru.javawebinar.topjava.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest extends TestCase {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(userMeal_1.getId(), USER_ID);
        assertMatch(meal, userMeal_1);
    }

    @Test
    public void getNotExist() {
        assertThrows(NotFoundException.class, () -> service.get(1, USER_ID));
    }

    @Test
    public void getByWrongUser() {
        assertThrows(NotFoundException.class, () -> service.get(userMeal_1.getId(), ADMIN_ID));
    }

    @Test
    public void delete() {
        Meal meal = userMeal_1;
        service.delete(meal.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(meal.getId(), USER_ID));
    }

    @Test
    public void deleteNotExist() {
        assertThrows(NotFoundException.class, () -> service.delete(1, USER_ID));
    }

    @Test
    public void deleteByWrongUser() {
        assertThrows(NotFoundException.class, () -> service.delete(userMeal_1.getId(), ADMIN_ID));
    }

    @Test
    public void getBetweenInclusiveAllRecords() {
        List<Meal> allUserMeals = Arrays.asList(userMeal_1, userMeal_2, userMeal_3, userMeal_4, userMeal_5, userMeal_6, userMeal_7);
        List<Meal> all = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(all, allUserMeals);
    }

    @Test
    public void getBetweenInclusivePartRecords() {
        List<Meal> filteredUserMeals = Arrays.asList(userMeal_1, userMeal_2, userMeal_3, userMeal_4);
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), LocalDate.of(2020, Month.JANUARY, 31), USER_ID);
        assertMatch(all, filteredUserMeals);
    }

    @Test
    public void getBetweenInclusiveNoRecords() {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2021, Month.JANUARY, 31), LocalDate.of(2022, Month.JANUARY, 31), USER_ID);
        assertMatch(all, Collections.emptyList());
    }

    @Test
    public void getAll() {
        List<Meal> userMeals = Arrays.asList(userMeal_1, userMeal_2, userMeal_3, userMeal_4, userMeal_5, userMeal_6, userMeal_7);
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, userMeals);
    }

    @Test
    public void getAllNoRecords() {
        List<Meal> all = service.getAll(GUEST_ID);
        assertMatch(all, Collections.emptyList());
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateNotExist() {
        Meal updated = getNew();
        updated.setId(1);
        assertThrows(NotFoundException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void updateByWrongUser() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class,
                () -> service.create(new Meal(null, userMeal_1.getDateTime(), "Ужин добавка", 999), USER_ID));
    }
}