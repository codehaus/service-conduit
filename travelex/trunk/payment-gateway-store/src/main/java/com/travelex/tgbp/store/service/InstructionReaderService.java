package com.travelex.tgbp.store.service;

import java.util.List;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.Currency;

/**
 * Reads the Instructions from the underlying data store.
 */
public interface InstructionReaderService {

    /**
     * Gives back all instructions on given currency which have not been sent out.
     *
     * @param currency - instruction currency.
     * @return list of instructions.
     */
    List<Instruction> findInstructionByCurrency(Currency currency);
}
