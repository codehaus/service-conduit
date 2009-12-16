package com.travelex.tgbp.output.impl.file;

import java.math.BigDecimal;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.file.BatchingConfigReaderService;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingService;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingServiceListener;
import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.service.InstructionReaderService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Abstract implementation for {@link OutputInstructionBatchingService}.
 */
public abstract class AbstractOutputInstructionBatchingService implements OutputInstructionBatchingService {

    @Reference protected BatchingConfigReaderService batchingConfigReaderService;
    @Reference protected InstructionReaderService instructionReaderService;

    @Callback protected OutputInstructionBatchingServiceListener serviceListener;

    private long thresholdCount;
    private BigDecimal thresholdAmount;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doBatching() {
        final OutputInstructionBatchingConfig config = batchingConfigReaderService.findByClearingMechanism(getClearingMechanism());
        this.thresholdCount = config.getThresholdCount();
        this.thresholdAmount = config.getThresholdAmount();
        batchOutputInstructions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

    }

    /**
     * Return supported clearing mechanism.
     *
     * @return supported clearing mechanism.
     */
    protected abstract ClearingMechanism getClearingMechanism();

    /*
     * Creates output submission objects meeting threshold count and threshold amount.
     */
    private void batchOutputInstructions(){
         List<OutputInstruction> outputInstructions = instructionReaderService.findOutputInstructionByClearingMechanism(getClearingMechanism());
         //TODO complete implementation
         //serviceListener.onOutputSubmission(`outputSubmission);
    }

}
