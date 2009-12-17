package com.travelex.tgbp.store.service.api;

import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.domain.Submission;

/**
 * Stores the Submission to the specified data store.
 */
public interface SubmissionStoreService {

    /**
     * Stores given submission.
     *
     * @param submission
     */
    Long store(Submission submission);

    /**
     * Stores given output submission.
     *
     * @param outputSubmission
     */
    void store(OutputSubmission outputSubmission);
}