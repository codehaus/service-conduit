package com.travelex.tgbp.output.service.file;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Call back interface for {@link OutputInstructionBatchingService}.
 */
@Conversational
public interface OutputInstructionBatchingServiceListener {

    /**
     * Notifies creation of a output submission object
     *
     * @param outputSubmission - output submission object.
     */
    void onOutputSubmission(OutputSubmission outputSubmission);

    /**
     * Notifies end of output instruction batching for given clearing mechanism
     *
     * @param clearingMechanism - clearing mechanism.
     */
    void onBatchingCompletion(ClearingMechanism clearingMechanism);

}
