package com.travelex.tgbp.output.impl;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.OutputInitiator;
import com.travelex.tgbp.output.service.OutputInstructionCollectorListener;
import com.travelex.tgbp.store.type.Currency;

/**
 * {@link OutputInitiator} implementation for {@link Currency#USD}.
 */
@Conversation
@Service (interfaces = {OutputInitiator.class, OutputInstructionCollectorListener.class})
public class USDOutputInitiator extends AbstractOutputInitiator {

    /**
     * {@inheritDoc}
     */
    @Override
    Currency getCurrencyType() {
        return Currency.USD;
    }

}
