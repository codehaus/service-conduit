package com.travelex.tgbp.output.impl.instruction;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInitiator;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationServiceListener;
import com.travelex.tgbp.store.type.Currency;

/**
 * {@link OutputInitiator} implementation for {@link Currency#USD}.
 */
@Conversation
@Service (interfaces = {OutputInitiator.class, OutputInstructionCreationServiceListener.class})
public class USDOutputInitiator extends AbstractOutputInitiator {

    /**
     * {@inheritDoc}
     */
    @Override
    Currency getCurrencyType() {
        return Currency.USD;
    }

}
