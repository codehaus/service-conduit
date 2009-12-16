package com.travelex.tgbp.output.impl;

import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.travelex.tgbp.output.service.OutputService;
import com.travelex.tgbp.output.service.instruction.OutputInitiator;
import com.travelex.tgbp.output.service.instruction.OutputInitiatorListener;
import com.travelex.tgbp.store.type.Currency;

/**
 * Default implementation of {@link OutputService}.
 */
@Service (interfaces = {OutputService.class, OutputInitiatorListener.class})
public class DefaultOutputService implements OutputService, OutputInitiatorListener {

    @Reference protected Map<Currency, OutputInitiator> initiators;

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
        for (OutputInitiator initiator : initiators.values()) {
            initiator.close();
        }
    }

}

