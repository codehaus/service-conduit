package com.travelex.tgbp.output.impl.instruction;

import java.util.HashMap;
import java.util.Map;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.store.domain.output.RemittanceAccountConfig;

/**
 * Abstract {@link OutputInstructionCreationService} implementation for MT103 based clearing mechanism.
 */
public abstract class AbstractMT103InstructionCreationService extends AbstractOutputInstructionCreationService {

    /**
     * {@inheritDoc}
     */
    @Override
    Map<String, Object> getTransformationContext() {
        final RemittanceAccountConfig outputConfig = outputConfigReader.findRemittanceConfigByClearingMechanism(getClearingMechanism());
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("root.element.name", PAYMENT_DATA_ROOT_ELEMENT);
        context.put("remitting.bank.account.number", outputConfig.getAccountNumber());
        context.put("remitting.bank.name", outputConfig.getBankName());
        return context;
    }

}
