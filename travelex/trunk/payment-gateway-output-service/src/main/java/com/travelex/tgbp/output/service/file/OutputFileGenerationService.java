package com.travelex.tgbp.output.service.file;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.OutputSubmission;

/**
 * Generates output submission file
 */
@Conversational
@Callback(OutputFileGenerationServiceListener.class)
public interface OutputFileGenerationService {

    /**
     * Generates output file for given output submission
     *
     * @param outputSubmission - submission whose output file to be created
     */
    void generate(OutputSubmission outputSubmission);

    /**
     * Closes current client conversation
     */
    @EndsConversation
    void close();

}
