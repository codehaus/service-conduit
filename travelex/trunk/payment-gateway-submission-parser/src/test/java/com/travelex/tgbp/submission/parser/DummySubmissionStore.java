package com.travelex.tgbp.submission.parser;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.SubmissionStoreService;

public class DummySubmissionStore implements SubmissionStoreService {

    @Reference protected Store store;

    public Long store(Submission s) {
        store.addSubmission(s);
        return 25L;
    }

    public void store(OutputSubmission arg0) {}

}
