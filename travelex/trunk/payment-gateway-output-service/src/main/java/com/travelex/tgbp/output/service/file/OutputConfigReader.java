package com.travelex.tgbp.output.service.file;

import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.output.types.RemittanceAccountConfig;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Service to find output process configuration parameters.
 */
public interface OutputConfigReader {

    /**
     * Finds output instruction batching configuration values on clearing mechanism.
     *
     * @param clearingMechanism - search criteria
     * @return found configuration object; null otherwise
     */
    OutputInstructionBatchingConfig findBatchingConfigByClearingMechanism(ClearingMechanism clearingMechanism);

    /**
     * Finds remittance account configuration values on clearing mechanism
     *
     * @param clearingMechanism - search criteria
     * @return found configuration object; null otherwise
     */
    RemittanceAccountConfig findRemittanceConfigByClearingMechanism(ClearingMechanism clearingMechanism);

}
