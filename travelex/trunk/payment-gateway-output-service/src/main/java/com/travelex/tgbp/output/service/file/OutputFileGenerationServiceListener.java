package com.travelex.tgbp.output.service.file;

import org.sca4j.api.annotation.scope.Conversation;

/**
 * Call back interface for {@link OutputFileGenerationService}.
 */
@Conversation
public interface OutputFileGenerationServiceListener {

    /**
     * Notifies end of file generation for output submission having given output submission id.
     *
     * @param outputSubmissionId - output submission.
     */
    void onFileGenerationCompletion(Long outputSubmissionId);

}
