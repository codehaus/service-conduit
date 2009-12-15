package com.travelex.tgbp.output.service.file;

import com.travelex.tgbp.store.domain.OutputSubmission;

/**
 * Generates output submission file
 */
public interface OutputFileGenerationService {

    /**
     * Generates output submission file for given output submission.
     *
     * @param outputSubmission - submission whose file to be created.
     */
    void generate(OutputSubmission outputSubmission);
}
