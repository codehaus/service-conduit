package com.travelex.tgb.event.notifier.api;

public interface EventNotifier {

    void onEvent(EventType eventType, EventDescriptor eventDescriptor);

}
