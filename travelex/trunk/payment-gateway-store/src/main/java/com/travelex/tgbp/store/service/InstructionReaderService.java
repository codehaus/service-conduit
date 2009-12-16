package com.travelex.tgbp.store.service;

import java.util.List;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Reads the Instructions from the underlying data store.
 */
public interface InstructionReaderService {

    /**
     * Gives back all input instructions on given currency.
     *
     * @param currency - instruction currency.
     * @return list of instructions.
     */
    List<Instruction> findInstructionByCurrency(Currency currency);

    /**
     * Gives back output instructions on given clearing mechanism which doesn't belong to any output submission.
     *
     * @param clearingMechanism - search criteria
     * @return list of output instructions.
     */
    List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism);
}
