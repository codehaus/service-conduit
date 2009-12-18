package com.travelex.tgbp.output.impl.file;

import org.joda.time.LocalDate;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.output.service.file.OutputFileGenerationService;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * File generation service capable of creating {@link ClearingMechanism#NACHA} acceptable file.
 */
@Conversation
@Service(interfaces = {OutputFileGenerationService.class})
public class NachaFileGenerationService extends AbstractOutputFileGenerationService {

    // %1$s - 6 - File creation date - YYMMDD
    private static final String FILE_HEADER = "101 0530002191569999999%1$s    A094101BANK OF AMERICA         TRAVELEX TGBP         DEPD0411";

    // %1$s - 6 - File creation date - YYMMDD
    // %2$s - 8 - Originating DFI (Bank ID)
    private static final String BATCH_HEADER = "5220TRAVELEX TGBP   DEPD0411 TRVXP      1569999999PPDPYTF MM123030114%1$s   1%2$s0000001";

    // %1$s - 6 - Total number of payment records (REC 6)
    // %2$s - 12 - Total Credit Entry Dollar Amount
    // %3$s - 8 - Originating DFI (Bank ID)
    private static final String BATCH_TRAILER = "8220%1$s0000000000000000000000%2$sTRAVELEX  0000000000000000000000000%3$s0000001";

    // %1$s - 6 - Total number of payment records (REC 6)
    // %2$s - 12 - Total Credit Entry Dollar Amount
    private static final String FILE_TRAILER = "900000100000100%1$s0000000000000000000000%2$s000000000000000000000000000000000000000";

    private static final String FILE_DATE_FORMAT = "YYMMdd";

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateFileContent(OutputSubmission outputSubmission) {
        final String fileCreationDate = new LocalDate().toString(FILE_DATE_FORMAT);
        final String originatingDfi = leftPadWithSpaceChar(outputConfigReader.findRemittanceConfigByClearingMechanism(ClearingMechanism.NACHA).getBankId(), 8);
        final String totalItemCount = leftPadWithSpaceChar(String.valueOf(outputSubmission.getTotalItemCount()), 6);
        final String totalCreditEntry = leftPadWithSpaceChar(outputSubmission.getTotalAmount().toPlainString(), 12);

        final StringBuilder buffer = new StringBuilder();
        buffer.append(String.format(FILE_HEADER, fileCreationDate));
        buffer.append(LINE_SEPARATOR);
        buffer.append(String.format(BATCH_HEADER, fileCreationDate, originatingDfi));
        buffer.append(LINE_SEPARATOR);
        appendTransactionRecords(outputSubmission.getOutputInstructions(), buffer);
        buffer.append(String.format(BATCH_TRAILER, totalItemCount, totalCreditEntry, originatingDfi));
        buffer.append(LINE_SEPARATOR);
        buffer.append(String.format(FILE_TRAILER, totalItemCount, totalCreditEntry));

        return buffer.toString();
    }

}
