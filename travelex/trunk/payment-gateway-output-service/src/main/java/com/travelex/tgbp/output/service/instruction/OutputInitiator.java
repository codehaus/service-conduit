package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;

/**
 * Resolves instruction output clearing mechanism and creates output instructions.
 */
@Conversational
@Callback(OutputInitiatorListener.class)
public interface OutputInitiator {

    /**
     * Performs below activities,
     *
     *  1. Searches input instructions on a currency which have not been sent for output yet.
     *  2. Resolves destination clearing mechanism for every instruction.
     *  3. Invokes related output instruction creation service.
     */
    void createOutputInstructions();

    /**
     * Closes current client conversation.
     */
    void close();
}
