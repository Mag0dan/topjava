package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MemoryIdMealStorage;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private static final Storage<Integer, Meal> storage = new MemoryIdMealStorage();
    private static final Logger log = getLogger(MealServlet.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void init() throws ServletException {
        super.init();
        MealsUtil.fillStorage(storage);
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
        final int calories = paramCalories.isEmpty() ? 0 : Integer.parseInt(paramCalories);
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
            request.setAttribute("formatter", dateTimeFormatter);
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
