package com.travelex.tgbp.output.impl.file;

import com.travelex.tgbp.store.domain.OutputSubmission;

/**
 * File generation service for creating MT103 files.
 */
public class MT103FileGenerationService extends AbstractOutputFileGenerationService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(OutputSubmission outputSubmission) {
        final StringBuilder buffer = new StringBuilder();
        appendTransactionRecords(outputSubmission.getOutputInstructions(), buffer);
        outputSubmission.setOutputFile(buffer.toString());
    }

}
