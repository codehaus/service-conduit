package com.travelex.tgbp.output.impl.instruction;

import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.instruction.OutputInstructionCollector;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCollectorListener;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationServiceListener;
import com.travelex.tgbp.routing.service.ClearingMechanismResolver;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.service.InstructionReaderService;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Abstract implementation for {@link OutputInstructionCreationService}.
 */
public abstract class AbstractOutputInstructionCreationService implements OutputInstructionCreationService, OutputInstructionCollectorListener {

    @Reference protected InstructionReaderService instructionReaderService;
    @Reference protected ClearingMechanismResolver clearingMechanismResolver;
    @Reference protected Map<ClearingMechanism, OutputInstructionCollector> instructionCollectors;

    @Callback protected OutputInstructionCreationServiceListener serviceListener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createInstructions() {
        final List<Instruction> inputInstructions = instructionReaderService.findInstructionByCurrency(getCurrencyType());
        for (Instruction instruction : inputInstructions) {
            instructionCollectors.get(clearingMechanismResolver.resolve(instruction)).addInstruction(instruction);
        }
        notifyCollectors();
        serviceListener.onCompletion(getCurrencyType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCompletion(ClearingMechanism clearingMechanism) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
       for (OutputInstructionCollector collector : instructionCollectors.values()) {
           collector.close();
       }
    }

    /**
     * Returns supported currency type by this output initiator.
     *
     * @return currency type.
     */
    abstract Currency getCurrencyType();

    /*
     * Notify end of instruction collection to individual collectors
     */
    private void notifyCollectors() {
        for (OutputInstructionCollector collector : instructionCollectors.values()) {
            collector.endCollection();
        }
    }

}
