package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;

/**
 * Call back interface for {@link OutputInstructionCollector}.
 */
@Conversational
public interface OutputInstructionCollectorListener {

    /**
     * Notifies end of output generation.
     */
    void onEnd();
}
