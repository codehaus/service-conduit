package com.travelex.tgbp.submission.parser;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.SubmissionStoreService;

public class DummySubmissionStore implements SubmissionStoreService {

    @Reference protected Store store;

    public Long store(Submission s) {
        final Long id = 25L;
        setPrivateField(s, "id", id);
        store.addSubmission(s);
        return id;
    }

    public void store(OutputSubmission arg0) {}

    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            PrivateAccessor.setField(object, fieldName, value);
        } catch (NoSuchFieldException e) {
            TestCase.fail(e.getMessage());
        }
    }

}
