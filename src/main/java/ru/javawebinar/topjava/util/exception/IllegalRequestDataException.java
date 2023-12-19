package ru.javawebinar.topjava.util.exception;

import java.util.List;

public class IllegalRequestDataException extends RuntimeException {
    private final List<String> details;

    public IllegalRequestDataException(String msg) {
        super(msg);
        this.details = List.of(msg);
    }

    public IllegalRequestDataException(List<String> msg) {
        this.details = msg;
    }

    public List<String> getDetails() {
        return details;
    }
}