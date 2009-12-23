package com.travelex.tgb.event.notifier;

import java.util.UUID;

import com.travelex.tgb.event.notifier.api.EventDescriptor;
import com.travelex.tgb.event.notifier.api.EventNotifier;
import com.travelex.tgb.event.notifier.api.EventNotifierException;
import com.travelex.tgb.event.notifier.api.EventType;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;

public class BlazeDSEventNotifier implements EventNotifier {

    @Override
    public void onEvent(EventType eventType, EventDescriptor eventDescriptor) {
        AsyncMessage message = populateMessage(eventType, eventDescriptor);
        dispatch(message);
    }

    private void dispatch(AsyncMessage message) {
        MessageBroker broker = MessageBroker.getMessageBroker(null);
        broker.routeMessageToService(message, null);
    }

    private AsyncMessage populateMessage(EventType eventType, EventDescriptor eventDescriptor) {
        AsyncMessage message = initialiseMessage(eventType);
        message.setBody(eventDescriptor);
        return message;
    }

    private AsyncMessage initialiseMessage(EventType eventType) {
        String destinationName = lookupDestinationName(eventType);

        AsyncMessage message = new AsyncMessage();
        message.setDestination(destinationName);

        message.setClientId("TGBP");
        message.setMessageId(UUID.randomUUID().toString());
        message.setTimestamp(System.currentTimeMillis());

        return message;
    }

    //This returns the name of the destination defined in the blazeds messaging config file.
    private String lookupDestinationName(EventType eventType) {
        //Could make this configurable in the composite.
        if(eventType == EventType.SYSTEM) {
            return "systemStatus";
        }
        else if(eventType == EventType.SUBMISSION) {
            return "submissionEvent";
        }

        throw new EventNotifierException("Unsupported event type");

    }
}
