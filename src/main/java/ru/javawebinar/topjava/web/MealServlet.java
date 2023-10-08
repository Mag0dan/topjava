package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
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

    private static final Storage<Integer, Meal> storage = MealsUtil.getStorage();
    private static final Logger log = getLogger(UserServlet.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String description = request.getParameter("description");
        String dateTime = request.getParameter("dateTime");
        String paramCalories = request.getParameter("calories");

        final boolean isCreate = (id.equals("0"));
        final int calories = paramCalories.isEmpty() ? 0 : Integer.parseInt(paramCalories);
        Meal meal;

        if (isCreate) {
            meal = new Meal(LocalDateTime.parse(dateTime), description, calories);
            storage.save(meal);
        } else {
            meal = storage.get(Integer.parseInt(id));
            meal.setCalories(calories);
            meal.setDescription(description);
            meal.setDateTime(LocalDateTime.parse(dateTime));
            storage.update(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String paramId = request.getParameter("id");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("meals", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY));
            request.setAttribute("formatter", dateTimeFormatter);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        Meal meal;
        switch (action) {
            case "delete":
                storage.delete(Integer.parseInt(paramId));
                response.sendRedirect("meals");
                return;
            case "edit":
                meal = storage.get(Integer.parseInt(paramId));
                break;
            case "add":
                meal = new Meal();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("/editMeal.jsp").forward(request, response);


//        response.sendRedirect("meals.jsp");
    }
}
