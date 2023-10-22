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
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
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
        Meal meal = service.get(userMeal.getId(), USER_ID);
        assertMatch(meal, userMeal);
    }

    @Test(expected = NotFoundException.class)
    public void getWrongUser() {
        service.get(userMeal.getId(), ADMIN_ID);
    }

    @Test
    public void delete() {
        Meal meal = userMeal;
        service.delete(meal.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(meal.getId(), USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void deleteWrongUser() {
        service.delete(userMeal.getId(), ADMIN_ID);
    }

    @Test
    public void getBetweenInclusiveAllRecords() {
        List<Meal> all = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(all, userMeals);

        all = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), LocalDate.of(2020, Month.JANUARY, 31), USER_ID);
        assertMatch(all, userMeals.subList(0, 4));

        all = service.getBetweenInclusive(LocalDate.of(2021, Month.JANUARY, 31), LocalDate.of(2022, Month.JANUARY, 31), USER_ID);
        assertMatch(all, Collections.EMPTY_LIST);
    }

    @Test
    public void getBetweenInclusivePartRecords() {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), LocalDate.of(2020, Month.JANUARY, 31), USER_ID);
        assertMatch(all, userMeals.subList(0, 4));
    }

    @Test
    public void getBetweenInclusiveNoRecords() {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2021, Month.JANUARY, 31), LocalDate.of(2022, Month.JANUARY, 31), USER_ID);
        assertMatch(all, Collections.EMPTY_LIST);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, userMeals);
    }

    @Test
    public void getAllNoRecords() {
        List<Meal> all = service.getAll(GUEST_ID);
        assertMatch(all, Collections.EMPTY_LIST);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test(expected = NotFoundException.class)
    public void updateWrongUser() {
        service.update(getUpdated(), ADMIN_ID);
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

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeCreate() {
        service.create(new Meal(null, userMeal.getDateTime(), "Ужин добавка", 999), USER_ID);
    }
}