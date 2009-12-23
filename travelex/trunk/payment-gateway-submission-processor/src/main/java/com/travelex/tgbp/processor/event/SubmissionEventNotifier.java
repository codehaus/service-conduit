package com.travelex.tgbp.processor.event;

import com.travelex.tgbp.store.domain.Submission;

public interface SubmissionEventNotifier {

    void onSubmissionCaptured(Submission submission);

}
