package com.travelex.tgbp.output.impl.file;

import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.file.OutputInstructionBatchingService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * {@link OutputInstructionBatchingService} implementation for {@link ClearingMechanism#BACS}.
 */
@Conversation
public class BACSInstructionBatchingService extends AbstractOutputInstructionBatchingService {

    /**
     * {@inheritDoc}
     */
    @Override
    protected ClearingMechanism getClearingMechanism() {
        return ClearingMechanism.BACS;
    }

}
