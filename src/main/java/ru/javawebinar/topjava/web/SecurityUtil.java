package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {

    private static Integer id;

    public static int authUserId() {
        if(id == null) {
            throw new NotFoundException("Need to authorize");
        }
        return id;
    }

    public static void setAuthUserId(int id) {
        SecurityUtil.id = id;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}