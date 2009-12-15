package com.travelex.tgbp.output.impl.instruction;

import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCollector;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionCollector} implementation for {@link ClearingMechanism#BACS}.
 */
@Conversation
public class BACSInstructionCollector extends AbstractOutputInstructionCollector {

    /**
     * {@inheritDoc}
     */
    @Override
    ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.BACS;
    }

}
