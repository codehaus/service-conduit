package com.travelex.tgbp.processor;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.Submission;

/**
 * Payment gateway input submission processor.
 */
@Conversational
public interface SubmissionProcessor {

    /**
     * Processes new input submission.
     *
     * @param submission
     */
    @EndsConversation
    void onSubmission(Submission submission);
}
