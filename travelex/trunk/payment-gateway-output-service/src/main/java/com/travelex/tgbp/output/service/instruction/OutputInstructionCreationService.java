package com.travelex.tgbp.output.service.instruction;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import com.travelex.tgbp.store.domain.Instruction;

/**
 * Accumulates and creates output instructions which will be sent out using same clearing mechanism.
 */
@Conversational
@Callback(OutputInstructionCreationServiceListener.class)
public interface OutputInstructionCreationService {

    /**
     * Adds payment instruction.
     *
     * @param instruction
     */
    void addInstruction(Instruction instruction);

    /**
     * Marks end of instruction accumulation, creates and stores output instructions for accumulated input instructions.
     */
    void onCollectionEnd();

    /**
     * Closes current client conversation.
     */
    @EndsConversation
    void close();
}
