package com.travelex.tgbp.output.service.file;

import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Service to find configured output instruction batching parameters.
 */
public interface BatchingConfigReaderService {

    /**
     * Finds configuration values on clearing mechanism.
     *
     * @param clearingMechanism - search criteria clearing mechanism.
     * @return found configuration object; null otherwise
     */
    OutputInstructionBatchingConfig findByClearingMechanism(ClearingMechanism clearingMechanism);

}
