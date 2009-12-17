package com.travelex.tgbp.output.impl.file;

import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * File generation service capable of creating {@link ClearingMechanism#FEDWIRE} acceptable file.
 */
public class FedwireFileGenerationService extends AbstractOutputFileGenerationService {

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
