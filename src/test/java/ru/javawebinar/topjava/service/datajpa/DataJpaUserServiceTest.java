package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.adminMeal1;
import static ru.javawebinar.topjava.MealTestData.adminMeal2;
import static ru.javawebinar.topjava.Profiles.DATAJPA;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        MEAL_MATCHER.assertMatch(user.getMeals(), adminMeal2, adminMeal1);
    }

    @Test
    public void getWithEmptyMeals() {
        User user = service.getWithMeals(GUEST_ID);
        USER_MATCHER.assertMatch(user, guest);
        MEAL_MATCHER.assertMatch(user.getMeals());
    }

    @Test
    public void getWithNotFound() {
        assertThrows(NotFoundException.class, () -> service.getWithMeals(NOT_FOUND));
    }
}
