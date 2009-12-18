package com.travelex.tgbp.output.impl.file;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.file.OutputFileGenerationService;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * File generation service capable of creating {@link ClearingMechanism#FEDWIRE} acceptable file.
 */
@Conversation
@Service(interfaces = {OutputFileGenerationService.class})
public class FedwireFileGenerationService extends AbstractOutputFileGenerationService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateFileContent(OutputSubmission outputSubmission) {
        final StringBuilder buffer = new StringBuilder();
        appendTransactionRecords(outputSubmission.getOutputInstructions(), buffer);
        return buffer.toString();
    }

}
