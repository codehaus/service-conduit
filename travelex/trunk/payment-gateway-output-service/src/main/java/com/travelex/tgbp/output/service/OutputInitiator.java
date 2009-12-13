package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;

/**
 * Kicks off payment output process for particular currency.
 */
@Conversational
@Callback(OutputInitiatorListener.class)
public interface OutputInitiator {

    /**
     * Initiates payment output process.
     *  1. Searches all instruction on a currency which have not been sent for output yet.
     *  2. Evaluates clearing mechanism for every instruction.
     *  3. Invokes related output instruction collector on evaluated clearing mechanism.
     */
    void initiate();

    /**
     * Closes current client conversation.
     */
    void close();
}
