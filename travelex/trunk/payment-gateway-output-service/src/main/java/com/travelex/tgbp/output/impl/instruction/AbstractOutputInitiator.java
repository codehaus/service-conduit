package com.travelex.tgbp.output.impl.instruction;

import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.instruction.OutputInitiator;
import com.travelex.tgbp.output.service.instruction.OutputInitiatorListener;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationServiceListener;
import com.travelex.tgbp.routing.service.ClearingMechanismResolver;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Abstract implementation for {@link OutputInitiator}.
 */
public abstract class AbstractOutputInitiator implements OutputInitiator, OutputInstructionCreationServiceListener {

    @Reference protected DataStore dataStore;
    @Reference protected ClearingMechanismResolver clearingMechanismResolver;
    @Reference protected Map<ClearingMechanism, OutputInstructionCreationService> instructionCollectors;

    @Callback protected OutputInitiatorListener serviceListener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createOutputInstructions() {
        final List<Instruction> inputInstructions = dataStore.findInstructionByCurrency(getCurrencyType());
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
        for (OutputInstructionCreationService collector : instructionCollectors.values()) {
            collector.onCollectionEnd();
        }
    }

}
