package com.travelex.tgbp.output.service.file;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.OutputSubmission;

/**
 * Groups output instructions on configured batching rules for a particular clearing mechanism.
 */
@Conversational
@Callback(OutputInstructionBatchingServiceListener.class)
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
     */
    void doBatching();

    /**
     * Closes current client conversation.
     */
    @EndsConversation
    void close();

}
