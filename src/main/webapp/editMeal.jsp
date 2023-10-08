<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${meal.id}">
    <h3>Время:</h3>
    <dl>
        <input type="datetime-local" name="dateTime" value="${meal.dateTime}">
    </dl>
    <h3>Описание:</h3>
    <dl>
        <input type="text" name="description" value="${meal.description}">
    </dl>
    <h3>Калории:</h3>
    <dl>
        <input type="number" name="calories" value="${meal.calories}">
    </dl>
    <button type="submit">Сохранить</button>
</form>
<button onclick="window.history.back()">Отменить</button>
</body>
</html>
