package com.travelex.tgbp.submission.parser;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.service.InstructionStoreService;

public class DummyInstructionStore implements InstructionStoreService {

    @Reference protected Store store;

    @Override
    public void store(Instruction i) {
        store.addInstruction(i);
    }

    @Override
    public void store(OutputInstruction arg0) {}

    @Override
    public void updateOutputInstructionId(Long arg0, Long arg1) {}
}
