package com.sakurafish.pockettoushituryou.shared.rxbus;

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