package com.travelex.tgbp.processor.event;

import java.util.List;

import org.joda.time.LocalDate;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgb.event.notifier.api.EventNotifier;
import com.travelex.tgb.event.notifier.api.EventType;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DefaultSubmissionEventNotifier implements SubmissionEventNotifier {

    @Reference protected EventNotifier eventNotifier;
    @Reference protected DataStore dataStore;

    public void onSubmissionCaptured(Submission submission) {

        List<String> totals = dataStore.getInstructionTotals(new LocalDate());
        String[] currencyValues = totals.toArray(new String[0]);

        int iCount = dataStore.getCount(Query.SUBMISSION_INSTRUCTION_COUNT, submission.getKey());
        int sCount = dataStore.getCount(Query.SUBMISSION_COUNT_BY_DATE, new LocalDate());

        SubmissionEvent se = new SubmissionEvent();
        se.setSubmissionId(submission.getMessageId());
        se.setCurrencyValues(currencyValues);
        se.setInstructionCount(iCount);
        se.setSubmissionCount(sCount);

        eventNotifier.onEvent(EventType.SUBMISSION, se);

    }

}
