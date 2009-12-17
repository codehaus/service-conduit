package com.travelex.tgbp.output.impl.instruction;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.store.domain.output.RemittanceAccountConfig;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionCreationService} implementation for {@link ClearingMechanism#FEDWIRE}.
 */
@Conversation
@Service (interfaces = {OutputInstructionCreationService.class})
public class FedwireInstructionCreationService extends AbstractOutputInstructionCreationService {

    /**
     * {@inheritDoc}
     */
    @Override
    ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.FEDWIRE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Map<String, Object> getTransformationContext() {
        final RemittanceAccountConfig outputConfig = outputConfigReader.findRemittanceConfigByClearingMechanism(getClearingMechanism());
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("root.element.name", PAYMENT_DATA_ROOT_ELEMENT);
        context.put("remitting.bank.id", outputConfig.getBankId());
        context.put("remitting.bank.name", outputConfig.getBankName());
        context.put("remitting.bank.account.number", outputConfig.getAccountNumber());
        context.put("remitting.bank.accountHolder.name", outputConfig.getAccountHolderName());
        context.put("remitting.bank.accountHolder.line1", outputConfig.getAccountHolderAddressLine1());
        context.put("remitting.bank.accountHolder.city", outputConfig.getAccountHolderCity());
        context.put("remitting.bank.accountHolder.state", outputConfig.getAccountHolderState());
        context.put("remitting.bank.accountHolder.zip", outputConfig.getAccountHolderZip());
        return context;
    }

}
