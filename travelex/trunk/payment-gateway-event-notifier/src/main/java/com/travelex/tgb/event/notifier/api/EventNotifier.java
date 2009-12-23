package com.travelex.tgb.event.notifier.api;

import java.io.Serializable;

public interface EventNotifier extends Serializable {

    void onEvent(EventType eventType, EventDescriptor eventDescriptor);

}
