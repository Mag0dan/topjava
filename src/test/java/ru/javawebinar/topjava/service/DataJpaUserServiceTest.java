package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.adminMeal1;
import static ru.javawebinar.topjava.MealTestData.adminMeal2;
import static ru.javawebinar.topjava.Profiles.DATAJPA;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getWithMeals() {
        User user = service.getUserWithMeal(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        MEAL_MATCHER.assertMatch(user.getMeals(), adminMeal1, adminMeal2);
    }

    @Test
    public void getWithEmptyMeals() {
        User user = service.getUserWithMeal(GUEST_ID);
        USER_MATCHER.assertMatch(user, guest);
        MEAL_MATCHER.assertMatch(user.getMeals());
    }
}
