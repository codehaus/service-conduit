package com.travelex.tgbp.submission.parser;

import java.io.InputStream;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.submission.parser.api.SubmissionParser;
import com.travelex.tgbp.submission.parser.api.SubmissionParserListener;

/**
 * Default implementation for {@link SubmissionParserClient}.
 */
@Conversation
@Service(interfaces = {SubmissionParserClient.class, SubmissionParserListener.class})
public class SubmissionParserClientImpl implements SubmissionParserClient, SubmissionParserListener {

    @Reference protected SubmissionParser submissionParser;
    @Reference protected DataStore dataStore;

    private Submission submission;

    @Override
    public void parse(InputStream inputData) {
        try {
            submissionParser.parse(inputData);
        } finally {
            submissionParser.close();
        }
    }

    @Override
    public void onInstruction(Instruction instruction) {
        instruction.setSubmissionId(submission.getKey());
        dataStore.store(instruction);
    }

    @Override
    public void onSubmissionHeader(String messageId, String submissionHeader) {
        submission = new Submission(null, null);
        submission.setMessageId(messageId);
        submission.setSubmissionHeader(submissionHeader);
        dataStore.store(submission);
    }

}
