package com.travelex.tgbp.store.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Represents group of instructions which are sent out in a same output file.
 */
public class OutputSubmission {

    private Long id;
    private ClearingMechanism clearingMechanism;

    private transient Set<OutputInstruction> outputInstructions = new HashSet<OutputInstruction>();

    /**
     * Initialises with attributes.
     *
     * @param clearingMechanism - target clearing mechanism.
     */
    public OutputSubmission(ClearingMechanism clearingMechanism) {
        this.clearingMechanism = clearingMechanism;
    }

    /**
     * Returns submission identifier.
     *
     * @return submission id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns target clearing mechanism.
     *
     * @return clearing mechanism.
     */
    public ClearingMechanism getClearingMechanism() {
        return clearingMechanism;
    }

    /**
     * Adds output instruction to this output submission
     *
     * @param instruction - output instruction
     */
    public void addOutputInstruction(OutputInstruction instruction) {
        outputInstructions.add(instruction);
    }

    /**
     * Returns output instructions contained in this submission.
     *
     * @return collection of output instructions.
     */
    public Collection<OutputInstruction> getOutputInstructions() {
        return Collections.unmodifiableCollection(outputInstructions);
    }

}
