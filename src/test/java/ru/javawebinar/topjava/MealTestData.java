package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final Meal userMeal_1 = new Meal(START_SEQ + 9, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal userMeal_2 = new Meal(START_SEQ + 8, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal userMeal_3 = new Meal(START_SEQ + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal userMeal_4 = new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal_5 = new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal userMeal_6 = new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal userMeal_7 = new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);


    public static final Meal adminMeal_1 = new Meal(START_SEQ + 16, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Админ Ужин", 410);
    public static final Meal adminMeal_2 = new Meal(START_SEQ + 15, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Админ Обед", 500);
    public static final Meal adminMeal_3 = new Meal(START_SEQ + 14, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Админ Завтрак", 1000);
    public static final Meal adminMeal_4 = new Meal(START_SEQ + 13, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Админ Еда на граничное значение", 100);
    public static final Meal adminMeal_5 = new Meal(START_SEQ + 12, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Админ Ужин", 500);
    public static final Meal adminMeal_6 = new Meal(START_SEQ + 11, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Админ Обед", 1000);
    public static final Meal adminMeal_7 = new Meal(START_SEQ + 10, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Админ Завтрак", 500);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 21, 0), "Новая еда", 1000);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal_1);
        updated.setDescription("Updated Завтрак");
        updated.setCalories(555);
        updated.setDateTime(LocalDateTime.of(2021, Month.OCTOBER, 31, 12, 55));
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
