package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String timeString) {
        if (timeString.isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeString);
    }
}
