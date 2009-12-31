package com.travelex.tgbp.output.service.event;

import java.util.List;

import org.joda.time.LocalDate;
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

        LocalDate today = new LocalDate();
        List<String> totals = dataStore.getOutputInstructionTotals();
        String[] currencyValues = totals.toArray(new String[0]);

        int outputCount = dataStore.getCount(Query.GET_OUTPUT_SUBMISSION_COUNT, today);
        OutputSubmission mostRecentSubmission = dataStore.getMostRecentOutputSubmission(today);

        if (mostRecentSubmission != null) {
            OutputEvent oe = new OutputEvent();
            oe.setCurrencyValues(currencyValues);
            oe.setOutputCount(outputCount);
            oe.setMostRecentSubId(String.valueOf(mostRecentSubmission.getKey()));
            oe.setMostRecentRoute(mostRecentSubmission.getClearingMechanism().name());
            oe.setMostRecentFileName(mostRecentSubmission.getFileName());
            oe.setMostRecentFileContent(new String(mostRecentSubmission.getOutputFile()));
            eventNotifier.onEvent(EventType.OUTPUT, oe);
        }
    }

}
