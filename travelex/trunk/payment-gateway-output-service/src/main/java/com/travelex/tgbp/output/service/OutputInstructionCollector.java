package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.Instruction;

/**
 * Accumulates payment instructions which will sent out using same clearing mechanism.
 */
@Conversational
@Callback(OutputInstructionCollectorListener.class)
public interface OutputInstructionCollector {

    /**
     * Adds payment instruction.
     *
     * @param instruction
     */
    void addInstruction(Instruction instruction);

    /**
     * Outputs accumulated instructions.
     */
    void generateOutput();

    /**
     * Closes current client conversation.
     */
    @EndsConversation
    void close();
}
