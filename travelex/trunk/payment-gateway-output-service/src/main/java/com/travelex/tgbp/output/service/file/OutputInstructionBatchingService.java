package com.travelex.tgbp.output.service.file;

import com.travelex.tgbp.store.domain.OutputSubmission;

/**
 * Groups output instructions on configured output rules.
 */
public interface OutputInstructionBatchingService {

    /**
     * Batches up output instructions and creates output submission objects on the basis of configured output rules.
     *
     * These rules contain below batching parameters,
     * <ul>
     *    <li>threshold count</li>
     *    <li>threshold amount</li>
     * </ul>
     *
     * One {@link OutputSubmission} object is created for one batch.
     *
     * @param message - command message
     */
    void doBatching(Object message);

}
