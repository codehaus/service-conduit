package com.travelex.tgbp.output.impl;

import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.OutputInitiator;
import com.travelex.tgbp.output.service.OutputInitiatorListener;
import com.travelex.tgbp.output.service.OutputInstructionCollector;
import com.travelex.tgbp.output.service.OutputInstructionCollectorListener;
import com.travelex.tgbp.routing.service.ClearingMechanismResolver;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.service.InstructionReaderService;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Abstract implementation for {@link OutputInitiator}.
 */
public abstract class AbstractOutputInitiator implements OutputInitiator, OutputInstructionCollectorListener {

    @Reference protected InstructionReaderService instructionReaderService;
    @Reference protected ClearingMechanismResolver clearingMechanismResolver;
    @Reference protected Map<ClearingMechanism, OutputInstructionCollector> instructionCollectors;

    @Callback protected OutputInitiatorListener outputInitiatorListener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initiate() {
        final List<Instruction> inputInstructions = instructionReaderService.findInstructionByCurrency(getCurrencyType());
        for (Instruction instruction : inputInstructions) {
            instructionCollectors.get(clearingMechanismResolver.resolve(instruction)).addInstruction(instruction);
        }
        generateOutput();
        outputInitiatorListener.onCompletion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOutputCompletion(ClearingMechanism clearingMechanism) {
        outputInitiatorListener.onCompletion(clearingMechanism.getCurrency());
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
     * Generates clearing mechanism specific output
     */
    private void generateOutput() {
        for (OutputInstructionCollector collector : instructionCollectors.values()) {
            collector.generateOutput();
        }
    }

}
