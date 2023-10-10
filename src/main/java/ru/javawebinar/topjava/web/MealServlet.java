package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MemoryMealStorage;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private static final Storage<Integer, Meal> storage = new MemoryMealStorage();
    private static final Logger log = getLogger(MealServlet.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void init() throws ServletException {
        Arrays.asList(
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                        new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 420))
                .forEach(storage::save);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        log.debug("doPost");
        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String description = request.getParameter("description");
        String dateTime = request.getParameter("dateTime");
        String paramCalories = request.getParameter("calories");

        final boolean isCreate = id == null || id.isEmpty();
        final int calories = Integer.parseInt(paramCalories);
        Meal meal;

        if (isCreate) {
            log.debug("create");
            meal = new Meal(LocalDateTime.parse(dateTime), description, calories);
        } else {
            log.debug("update");
            meal = new Meal(Integer.parseInt(id), LocalDateTime.parse(dateTime), description, calories);
        }
        storage.save(meal);
        log.debug("redirect to meals");
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet");
        String action = request.getParameter("action");
        if (action == null) {
            log.debug("action null");
            log.debug("redirect to meals");
            request.setAttribute("meals", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY));
            request.setAttribute("formatter", DATE_TIME_FORMATTER);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        Meal meal;
        switch (action) {
            case "delete":
                log.debug("action delete");
                storage.delete(Integer.parseInt(request.getParameter("id")));
                log.debug("redirect to meals");
                response.sendRedirect("meals");
                return;
            case "edit":
                log.debug("action edit");
                meal = storage.get(Integer.parseInt(request.getParameter("id")));
                break;
            case "add":
                log.debug("action add");
                meal = new Meal();
                break;
            default:
                log.debug("action default");
                log.debug("redirect to meals");
                response.sendRedirect("meals");
                return;
        }
        log.debug("redirect to editMeal");
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
    }
}
