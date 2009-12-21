package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * Trigger service to create output instructions.
 */
@Conversational
public interface InstructionOutputService {

    /**
     * Triggers output instruction creation process.
     *
     * @param message - notification message.
     */
    @EndsConversation
    void outputInstructions(Object message);
}
