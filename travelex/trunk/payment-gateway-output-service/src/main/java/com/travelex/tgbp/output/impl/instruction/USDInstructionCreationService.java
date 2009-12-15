package com.travelex.tgbp.output.impl.instruction;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCollectorListener;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.store.type.Currency;

/**
 * {@link OutputInstructionCreationService} implementation for {@link Currency#USD}.
 */
@Conversation
@Service (interfaces = {OutputInstructionCreationService.class, OutputInstructionCollectorListener.class})
public class USDInstructionCreationService extends AbstractOutputInstructionCreationService {

    /**
     * {@inheritDoc}
     */
    @Override
    Currency getCurrencyType() {
        return Currency.USD;
    }

}
