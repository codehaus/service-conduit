package com.travelex.tgbp.store.service;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;

/**
 * Stores the Instruction to the specified data store.
 */
public interface InstructionStoreService {

    /**
     * Stores given instruction.
     *
     * @param instruction
     */
    void store(Instruction instruction);

    /**
     * Stores given output instruction.
     *
     * @param outputInstruction
     */
    void store(OutputInstruction outputInstruction);

    /**
     * Updates instruction with associated output instruction id.
     *
     * @param instructionId - id of instruction to update.
     * @param outputInstructionId - id of corresponding output instruction.
     */
    void updateOutputInstructionId(Long instructionId, Long outputInstructionId);

}
