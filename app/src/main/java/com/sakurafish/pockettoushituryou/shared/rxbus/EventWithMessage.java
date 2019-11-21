package com.sakurafish.pockettoushituryou.shared.rxbus;

@Deprecated
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