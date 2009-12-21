package com.travelex.tgbp.submission.parser;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;

public class Pain001SubmissionParserITest extends TestCase {

    @Reference protected SubmissionParserClient submissionParserClient;
    @Reference protected Store store;

    public void testParsing() throws Exception {
        InputStream submissionFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("source-pain001.xml");
        submissionParserClient.parse(submissionFile);

        List<Submission> submissions = store.getSubmissions();
        assertEquals("Unexpected submission count", 1, submissions.size());
        assertTrue("Incorrect header", submissions.get(0).getSubmissionHeader().trim().startsWith("<CstmrCdtTrfInitn"));

        List<Instruction> instructions = store.getInstructions();
        assertEquals("Unexpected instruction count", 3, instructions.size());

        for (Instruction instruction : instructions) {
            assertTrue("Incorrect payment data", instruction.getPaymentData().trim().startsWith("<CdtTrfTxInf"));
            assertTrue("Incorrect payment group data", instruction.getPaymentGroupData().trim().startsWith("<PmtInf"));

            assertEquals("Incorrect submission id", instruction.getSubmissionId().longValue(), 25);
        }

    }

}
