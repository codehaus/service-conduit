package com.travelex.tgbp.output.impl;

import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.travelex.tgbp.output.service.OutputService;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationServiceListener;
import com.travelex.tgbp.store.type.Currency;

/**
 * Default implementation of {@link OutputService}.
 */
@Service (interfaces = {OutputService.class, OutputInstructionCreationServiceListener.class})
public class DefaultOutputService implements OutputService, OutputInstructionCreationServiceListener {

    @Reference protected Map<Currency, OutputInstructionCreationService> outputInstructionCreators;

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputInstructions(Object message) {
        try {
            for (OutputInstructionCreationService creator : outputInstructionCreators.values()) {
                creator.createInstructions();
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
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCompletion(Currency currency) {

    }

    /*
     * Closes service conversations.
     */
    private void closeConversations() {
        for (OutputInstructionCreationService creator : outputInstructionCreators.values()) {
            creator.close();
        }
    }

}

