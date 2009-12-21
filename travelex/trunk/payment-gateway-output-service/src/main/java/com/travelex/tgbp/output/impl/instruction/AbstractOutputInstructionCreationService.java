package com.travelex.tgbp.output.impl.instruction;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.file.OutputConfigReader;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationService;
import com.travelex.tgbp.output.service.instruction.OutputInstructionCreationServiceListener;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.service.api.InstructionStoreService;
import com.travelex.tgbp.store.service.api.SubmissionStoreService;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.transform.api.TransformationService;

/**
 * Abstract implementation for {@link OutputInstructionCreationService}.
 */
public abstract class AbstractOutputInstructionCreationService implements OutputInstructionCreationService {

    @Reference protected SubmissionStoreService submissionStoreService;
    @Reference protected InstructionStoreService instructionStoreService;
    @Reference protected TransformationService<Source, String, Map<String, Object>> paymentDataTransformer;
    @Reference protected OutputConfigReader outputConfigReader;

    @Callback protected OutputInstructionCreationServiceListener serviceListener;

    private final Set<Instruction> instructions = new HashSet<Instruction>();

    private static final String FILE_DATE_FORMAT = "YYMMdd";
    protected static final String PAYMENT_DATA_ROOT_ELEMENT = "CdtTrfTxInf";

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
    public void onCollectionEnd() {
        if (!instructions.isEmpty()) {
            createOutputInstructions();
            serviceListener.onCompletion(getClearingMechanism());
        }
    }

    /**
     * Returns supported clearing mechanism.
     *
     * @return clearing mechanism.
     */
    abstract ClearingMechanism getClearingMechanism();

    /**
     * Returns payment data transformation context
     *
     * @return transformation context
     */
    abstract Map<String, Object> getTransformationContext();

    /*
     * Prepares and stores output instructions for accumulated input instructions.
     */
    private void createOutputInstructions() {
        final Map<String, Object> transformationContext = getTransformationContext();
        for (Instruction instruction : instructions) {
            transformationContext.put("payment.value.date", instruction.getValueDate().toString(FILE_DATE_FORMAT));
            OutputInstruction outputInstruction = new OutputInstruction(getClearingMechanism(), null, null, instruction.getAmount());
            outputInstruction.setOutputPaymentData(paymentDataTransformer.transform(transformationContext, new StreamSource(new ByteArrayInputStream(instruction.getPaymentData().getBytes()))));
            instructionStoreService.store(outputInstruction);
            instructionStoreService.updateOutputInstructionId(instruction.getKey(), outputInstruction.getId());
        }
    }
}
