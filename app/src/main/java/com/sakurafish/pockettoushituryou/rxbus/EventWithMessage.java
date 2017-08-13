package com.sakurafish.pockettoushituryou.rxbus;

public class EventWithMessage {
    private String message;

    public EventWithMessage() {
    }

    public EventWithMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}