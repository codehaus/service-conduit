package com.travelex.tgbp.output.impl;

import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.travelex.tgbp.output.service.OutputService;
import com.travelex.tgbp.output.service.file.OutputFileGenerationService;
import com.travelex.tgbp.output.service.file.OutputFileGenerationServiceListener;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingService;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingServiceListener;
import com.travelex.tgbp.output.service.instruction.OutputInitiator;
import com.travelex.tgbp.output.service.instruction.OutputInitiatorListener;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Default implementation of {@link OutputService}.
 */
@Service (interfaces = {OutputService.class, OutputInitiatorListener.class, OutputInstructionBatchingServiceListener.class, OutputFileGenerationServiceListener.class })
public class DefaultOutputService implements OutputService, OutputInitiatorListener, OutputInstructionBatchingServiceListener, OutputFileGenerationServiceListener {

    @Reference protected Map<Currency, OutputInitiator> initiators;
    @Reference protected Map<ClearingMechanism, OutputInstructionBatchingService> instructionBatchers;
    @Reference protected Map<ClearingMechanism, OutputFileGenerationService> fileGenerators;

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputInstructions(Object message) {
        try {
            for (OutputInitiator initiator : initiators.values()) {
              initiator.createOutputInstructions();
            }
        } finally {
            closeConversations();
        }
    }

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
    public void onCompletion(Currency currency) {

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
        for (OutputInitiator initiator : initiators.values()) {
            initiator.close();
        }

        for (OutputInstructionBatchingService instructionBatcher : instructionBatchers.values()) {
            instructionBatcher.close();
        }

        for (OutputFileGenerationService fileGenerator : fileGenerators.values()) {
            fileGenerator.close();
        }
    }

}

