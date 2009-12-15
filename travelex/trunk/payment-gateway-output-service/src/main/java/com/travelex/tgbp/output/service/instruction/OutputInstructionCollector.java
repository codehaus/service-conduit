package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.Instruction;

/**
 * Accumulates payment instructions which will be sent out using same clearing mechanism.
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
     * Create and store output instructions for  accumulated input instructions.
     */
    void endCollection();

    /**
     * Closes current client conversation.
     */
    @EndsConversation
    void close();
}
