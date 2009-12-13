package com.travelex.tgbp.store.service;

import com.travelex.tgbp.store.domain.Instruction;

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
     * Updates instruction with associated output instruction id.
     *
     * @param instructionId - id of instruction to update.
     */
    void updateOutputInstructionId(Long instructionId);

}
