package com.travelex.tgbp.output.impl;

import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.OutputInstructionCollector;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionCollector} implementation for {@link ClearingMechanism#BACS}.
 */
@Conversation
public class BACSOutputInstructionCollector extends AbstractOutputInstructionCollector {

    /**
     * {@inheritDoc}
     */
    @Override
    ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.BACS;
    }

}
