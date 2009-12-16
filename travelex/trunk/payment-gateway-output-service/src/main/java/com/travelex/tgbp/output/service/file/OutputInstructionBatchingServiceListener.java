package com.travelex.tgbp.output.service.file;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.domain.OutputSubmission;

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

}
