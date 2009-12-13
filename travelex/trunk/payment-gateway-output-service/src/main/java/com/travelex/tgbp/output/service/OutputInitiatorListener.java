package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;

/**
 * Call back listener for {@link OutputInitiator}.
 */
@Conversational
public interface OutputInitiatorListener {

    /**
     * Notifies end of output processing.
     */
    void onEnd();
}
