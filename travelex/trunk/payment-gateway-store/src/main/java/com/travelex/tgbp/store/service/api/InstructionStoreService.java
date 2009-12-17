package com.travelex.tgbp.store.service.api;

import java.util.Collection;

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
     * Updates input instruction with associated output instruction id.
     *
     * @param inputInstructionId - id of input instruction to update.
     * @param outputInstructionId - id of corresponding output instruction.
     */
    void updateOutputInstructionId(Long inputInstructionId, Long outputInstructionId);

    /**
     * Updates output instructions with output submission id.
     *
     * @param outputSubmissionId - id of output submission to which this output instruction belong to.
     * @param outputInstructions - collection of output instructions to be updated.
     */
    void updateOutputSubmissionId(Long outputSubmissionId, Collection<OutputInstruction> outputInstructions);

}
