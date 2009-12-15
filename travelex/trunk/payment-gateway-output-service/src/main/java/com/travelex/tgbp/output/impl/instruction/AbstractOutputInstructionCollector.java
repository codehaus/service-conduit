package com.travelex.tgbp.output.impl.instruction;

import java.util.HashSet;
import java.util.Set;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCollector;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCollectorListener;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.service.InstructionStoreService;
import com.travelex.tgbp.store.service.SubmissionStoreService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Abstract implementation for {@link OutputInstructionCollector}.
 */
public abstract class AbstractOutputInstructionCollector implements OutputInstructionCollector {

    @Reference protected SubmissionStoreService submissionStoreService;
    @Reference protected InstructionStoreService instructionStoreService;

    @Callback protected OutputInstructionCollectorListener serviceListener;

    private Set<Instruction> instructions = new HashSet<Instruction>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInstruction(Instruction instruction) {
         instructions.add(instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endCollection() {
        if (!instructions.isEmpty()) {
            createOutputInstructions();
            serviceListener.onCompletion(getClearingMechanism());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
         instructions = null;
    }

    /**
     * Returns supported clearing mechanism.
     *
     * @return clearing mechanism.
     */
    abstract ClearingMechanism getClearingMechanism();

    /*
     * Prepares and stores output instructions for accumulated input instructions.
     */
    private void createOutputInstructions() {
        for (Instruction instruction : instructions) {
            final OutputInstruction outputInstruction = new OutputInstruction(getClearingMechanism(), null, null, instruction.getAmount());
            instructionStoreService.store(outputInstruction);
            instructionStoreService.updateOutputInstructionId(instruction.getId(), outputInstruction.getId());
        }
    }

}
