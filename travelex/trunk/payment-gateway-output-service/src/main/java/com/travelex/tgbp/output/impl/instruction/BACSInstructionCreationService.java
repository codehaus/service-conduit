package com.travelex.tgbp.output.impl.instruction;

import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionCreationService} implementation for {@link ClearingMechanism#BACS}.
 */
@Conversation
@Service (interfaces = {OutputInstructionCreationService.class})
public class BACSInstructionCreationService extends AbstractOutputInstructionCreationService {

    /**
     * {@inheritDoc}
     */
    @Override
    ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.BACS;
    }

}
