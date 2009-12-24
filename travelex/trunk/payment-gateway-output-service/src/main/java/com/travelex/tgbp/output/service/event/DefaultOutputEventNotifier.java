package com.travelex.tgbp.output.service.event;

import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgb.event.notifier.api.EventNotifier;
import com.travelex.tgb.event.notifier.api.EventType;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DefaultOutputEventNotifier implements OutputEventNotifier {

    @Reference protected EventNotifier eventNotifier;
    @Reference protected DataStore dataStore;

    public void onOutput() {

        List<String> totals = dataStore.getOutputInstructionTotals();
        String[] currencyValues = totals.toArray(new String[0]);

        int outputCount = dataStore.getCount(Query.GET_OUTPUT_SUBMISSION_COUNT);
        String mostRecentRoute = dataStore.getMostRecentRoute();

        OutputEvent oe = new OutputEvent();
        oe.setCurrencyValues(currencyValues);
        oe.setOutputCount(outputCount);
        oe.setMostRecentRoute(mostRecentRoute);

        eventNotifier.onEvent(EventType.OUTPUT, oe);

    }

}
