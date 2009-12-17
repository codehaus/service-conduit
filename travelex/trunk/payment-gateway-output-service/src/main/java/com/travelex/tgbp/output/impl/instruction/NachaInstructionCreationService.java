package com.travelex.tgbp.output.impl.instruction;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionCreationService} implementation for {@link ClearingMechanism#NACHA}.
 */
@Conversation
@Service (interfaces = {OutputInstructionCreationService.class})
public class NachaInstructionCreationService extends AbstractOutputInstructionCreationService {

    /**
     * {@inheritDoc}
     */
    @Override
    ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.NACHA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Map<String, Object> getTransformationContext() {
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("root.element.name", PAYMENT_DATA_ROOT_ELEMENT);
        return context;
    }

}
