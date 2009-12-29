package com.travelex.tgbp.output.service.event;

import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgb.event.notifier.api.EventNotifier;
import com.travelex.tgb.event.notifier.api.EventType;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DefaultOutputEventNotifier implements OutputEventNotifier {

    @Reference protected EventNotifier eventNotifier;
    @Reference protected DataStore dataStore;

    public void onOutput() {

        List<String> totals = dataStore.getOutputInstructionTotals();
        String[] currencyValues = totals.toArray(new String[0]);

        int outputCount = dataStore.getCount(Query.GET_OUTPUT_SUBMISSION_COUNT);
        OutputSubmission mostRecentSubmission = dataStore.getMostRecentOutputSubmission();

        OutputEvent oe = new OutputEvent();
        oe.setCurrencyValues(currencyValues);
        oe.setOutputCount(outputCount);
        oe.setMostRecentRoute(mostRecentSubmission.getClearingMechanism().name());
        oe.setMostRecentFileName(mostRecentSubmission.getFileName());
        oe.setMostRecentFileContent(new String(mostRecentSubmission.getOutputFile()));

        eventNotifier.onEvent(EventType.OUTPUT, oe);

    }

}
