package com.travelex.tgbp.output.impl.file;

import java.math.BigDecimal;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.file.OutputConfigReader;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingService;
import com.travelex.tgbp.output.service.file.OutputInstructionBatchingServiceListener;
import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.service.InstructionReaderService;
import com.travelex.tgbp.store.service.InstructionStoreService;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * Abstract implementation for {@link OutputInstructionBatchingService}.
 */
public abstract class AbstractOutputInstructionBatchingService implements OutputInstructionBatchingService {

    @Reference protected OutputConfigReader outputConfigReader;
    @Reference protected InstructionReaderService instructionReaderService;
    @Reference protected InstructionStoreService instructionStoreService;

    @Callback protected OutputInstructionBatchingServiceListener serviceListener;

    private long thresholdCount;
    private BigDecimal thresholdAmount;

    private long currentBatchCount;
    private BigDecimal currentBatchAmount;
    private OutputSubmission currentOutputSubmission;

    private List<OutputInstruction> outputInstructions;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doBatching() {
        setBatchThresholdLimits();
        this.outputInstructions = instructionReaderService.findOutputInstructionByClearingMechanism(getClearingMechanism());
        batchOutputInstructions();
        serviceListener.onBatchingCompletion(getClearingMechanism());
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
     * Evaluates batch threshold limits (max total item count and max total item amount)
     */
    private void setBatchThresholdLimits() {
        final OutputInstructionBatchingConfig config = outputConfigReader.findBatchingConfigByClearingMechanism(getClearingMechanism());
        this.thresholdCount = config.getThresholdCount();
        this.thresholdAmount = config.getThresholdAmount();
    }

    /*
     * Creates output submission objects meeting batch threshold limits.
     */
    private void batchOutputInstructions(){
       if (!outputInstructions.isEmpty()) {
           resetCurrentBatch();
           for (OutputInstruction outputInstruction : outputInstructions) {
                addToCurrentBatch(outputInstruction);
                if (isCurrentBatchFull()) {
                    flushCurrentBatch();
                }
           }
       }
    }

    /*
     * Checks if current batch has reached its threshold limit
     */
    private boolean isCurrentBatchFull() {
       return currentBatchCount >= thresholdCount || currentBatchAmount.compareTo(thresholdAmount) >= 0;
    }

    /*
     * Resets current batch parameters
     */
    private void resetCurrentBatch() {
       currentBatchCount = 0;
       currentBatchAmount = BigDecimal.ZERO;
       currentOutputSubmission = new OutputSubmission(getClearingMechanism());
    }

    /*
     * Adds given output instruction to current batch
     */
    private void addToCurrentBatch(OutputInstruction instruction) {
       currentBatchCount++;
       currentBatchAmount = currentBatchAmount.add(instruction.getAmount());
       currentOutputSubmission.addOutputInstruction(instruction);
    }

    /*
     * Hands off current batch
     */
    private void flushCurrentBatch() {
       currentOutputSubmission.setTotalItemCount(currentBatchCount);
       currentOutputSubmission.setTotalAmount(currentBatchAmount);
       serviceListener.onOutputSubmission(currentOutputSubmission);
       resetCurrentBatch();
    }

}
