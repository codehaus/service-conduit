package com.travelex.tgbp.output.impl.file;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.file.OutputConfigReader;
import com.travelex.tgbp.output.service.file.OutputFileGenerationService;
import com.travelex.tgbp.store.domain.OutputInstruction;

/**
 * Abstract implementation for {@link OutputFileGenerationService}.
 */
public abstract class AbstractOutputFileGenerationService implements OutputFileGenerationService {

    @Reference protected OutputConfigReader outputConfigReader;

    private static final char SPACE_CHAR = ' ';
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Appends output instruction payment data to file buffer.
     *
     * @param instructions - collection of output instructions.
     * @param buffer - output file buffer
     */
    protected void appendTransactionRecords(Collection<OutputInstruction> instructions, StringBuilder buffer) {
        for (OutputInstruction instruction : instructions) {
            buffer.append(instruction.getOutputPaymentData());
            buffer.append(LINE_SEPARATOR);
        }
    }

    /**
     * Pads space characters to given string value.
     *
     * @param value - string value to pad.
     * @param desiredLength - desired final string size.
     * @return padded string
     */
    protected String leftPadWithSpaceChar(String value, int desiredLength) {
        return StringUtils.leftPad(value, desiredLength, SPACE_CHAR);
    }

}
