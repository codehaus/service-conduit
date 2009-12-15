package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;

/**
 * Creates output instructions on a particular currency.
 */
@Conversational
@Callback(OutputInstructionCreationServiceListener.class)
public interface OutputInstructionCreationService {

    /**
     * Performs below activities,
     *
     *  1. Searches all instruction on a currency which have not been sent for output yet.
     *  2. Resolves destination clearing mechanism for every instruction.
     *  3. Invokes related output instruction collector.
     */
    void createInstructions();

    /**
     * Closes current client conversation.
     */
    void close();
}
