package com.travelex.tgbp.output.impl;

import java.util.HashSet;
import java.util.Set;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.OutputInstructionCollector;
import com.travelex.tgbp.output.service.OutputInstructionCollectorListener;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.service.InstructionStoreService;
import com.travelex.tgbp.store.service.SubmissionStoreService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Abstract implementation for {@link OutputInstructionCollector}.
 */
public abstract class AbstractOutputInstructionCollector implements OutputInstructionCollector {

    @Reference protected SubmissionStoreService submissionStoreService;
    @Reference protected InstructionStoreService instructionStoreService;

    @Callback protected OutputInstructionCollectorListener listener;

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
    public void generateOutput() {
        if (!instructions.isEmpty()) {
            createOutputItems();
            // TODO - Invoke output file generation service.
            listener.onOutputCompletion(getClearingMechanism());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

    }

    /**
     * Returns supported clearing mechanism.
     *
     * @return clearing mechanism.
     */
    abstract ClearingMechanism getClearingMechanism();

    /*
     * Prepares and stores output domain objects for accumulated instructions.
     */
    private void createOutputItems() {
        final OutputSubmission outputSubmission = new OutputSubmission(getClearingMechanism());
        submissionStoreService.store(outputSubmission);

        for (Instruction instruction : instructions) {
            final OutputInstruction outputInstruction = new OutputInstruction(outputSubmission.getId(), null, null, instruction.getAmount());
            instructionStoreService.store(outputInstruction);
            instructionStoreService.updateOutputInstructionId(instruction.getId(), outputInstruction.getId());
        }
    }

}
