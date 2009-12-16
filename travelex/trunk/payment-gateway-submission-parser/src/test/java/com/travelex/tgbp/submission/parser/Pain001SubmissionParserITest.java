package com.travelex.tgbp.submission.parser;

import java.io.InputStream;
import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.submission.parser.api.SubmissionParser;

import junit.framework.TestCase;

public class Pain001SubmissionParserITest extends TestCase {

    @Reference protected SubmissionParser submissionParser;
    @Reference protected Store store;

    public void testParsing() throws Exception {
        InputStream submissionFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("source-pain001.xml");
        submissionParser.parse(submissionFile);

        List<Submission> submissions = store.getSubmissions();
        assertEquals("Unexpected submission count", 1, submissions.size());

        List<Instruction> instructions = store.getInstructions();
        assertEquals("Unexpected instruction count", 3, instructions.size());

        for (Instruction instruction : instructions) {
            assertTrue("Incorrect payment data", instruction.getPaymentData().trim().startsWith("<CdtTrfTxInf"));
            assertTrue("Incorrect payment group data", instruction.getPaymentGroupData().trim().startsWith("<PmtInf"));

            assertEquals("Incorrect submission id", instruction.getSubmissionId().longValue(), 25);
        }

    }

}
