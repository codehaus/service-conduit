package com.travelex.tgbp.output.impl;

import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.FileOutputService;
import com.travelex.tgbp.output.service.file.OutputFileGenerationService;
import com.travelex.tgbp.output.service.file.OutputFileGenerationServiceListener;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingService;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingServiceListener;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Default implementation for {@link FileOutputService}.
 */
@Conversation
@Service (interfaces = {FileOutputService.class, OutputInstructionBatchingServiceListener.class, OutputFileGenerationServiceListener.class })
public class DefaultFileOutputService implements FileOutputService, OutputInstructionBatchingServiceListener, OutputFileGenerationServiceListener {

    @Reference protected Map<ClearingMechanism, OutputInstructionBatchingService> instructionBatchers;
    @Reference protected Map<ClearingMechanism, OutputFileGenerationService> fileGenerators;

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputFiles(Object message) {
        try {
            for (OutputInstructionBatchingService instructionBatcher : instructionBatchers.values()) {
                instructionBatcher.doBatching();
            }
        } finally {
            closeConversations();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOutputSubmission(OutputSubmission outputSubmission) {
        fileGenerators.get(outputSubmission.getClearingMechanism()).generate(outputSubmission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBatchingCompletion(ClearingMechanism clearingMechanism) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileGenerationCompletion(Long outputSubmissionId) {

    }

    /*
     * Closes service conversations.
     */
    private void closeConversations() {
        for (OutputInstructionBatchingService instructionBatcher : instructionBatchers.values()) {
            instructionBatcher.close();
        }

        for (OutputFileGenerationService fileGenerator : fileGenerators.values()) {
            fileGenerator.close();
        }
    }

}
