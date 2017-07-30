package com.sakurafish.pockettoushituryou.rxbus;

public class FoodsUpdatedEvent {
    private String message;

    public FoodsUpdatedEvent() {
    }

    public FoodsUpdatedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}