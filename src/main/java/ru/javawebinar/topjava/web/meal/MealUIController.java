package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = "/profile/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealUIController extends AbstractMealController {

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

//    @GetMapping("/update")
//    public String update(HttpServletRequest request, Model model) {
//        model.addAttribute("meal", super.get(getId(request)));
//        return "mealForm";
//    }

//    @GetMapping("/create")
//    public String create(Model model) {
//        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
//        return "mealForm";
//    }

//    @PostMapping
//    public String updateOrCreate(HttpServletRequest request) {
//        Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime")),
//                request.getParameter("description"),
//                Integer.parseInt(request.getParameter("calories")));
//
//        if (request.getParameter("id").isEmpty()) {
//            super.create(meal);
//        } else {
//            super.update(meal, getId(request));
//        }
//        return "redirect:/meals";
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                       @RequestParam String description,
                       @RequestParam int calories) {
        super.create(new Meal(dateTime, description, calories));
    }

    @Override
    @GetMapping("/filter")
    public List<MealTo> getBetween(
            @RequestParam @Nullable LocalDate startDate,
            @RequestParam @Nullable LocalTime startTime,
            @RequestParam @Nullable LocalDate endDate,
            @RequestParam @Nullable LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }

//    @GetMapping("/filter")
//    public String getBetween(HttpServletRequest request, Model model) {
//        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
//        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
//        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
//        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
//        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
//        return "meals";
//    }

//    private int getId(HttpServletRequest request) {
//        String paramId = Objects.requireNonNull(request.getParameter("id"));
//        return Integer.parseInt(paramId);
//    }
}
