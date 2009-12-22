package com.travelex.tgb.event.notifier.api;

public class EventNotifierException extends RuntimeException {

    public EventNotifierException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EventNotifierException(String arg0) {
        super(arg0);
    }

}
