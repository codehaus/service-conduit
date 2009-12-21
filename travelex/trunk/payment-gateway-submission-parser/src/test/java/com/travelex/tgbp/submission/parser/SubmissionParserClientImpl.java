package com.travelex.tgbp.submission.parser;

import java.io.InputStream;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.InstructionStoreService;
import com.travelex.tgbp.store.service.api.SubmissionStoreService;
import com.travelex.tgbp.submission.parser.api.SubmissionParser;
import com.travelex.tgbp.submission.parser.api.SubmissionParserListener;

/**
 * Default implementation for {@link SubmissionParserClient}.
 */
@Conversation
@Service(interfaces = {SubmissionParserClient.class, SubmissionParserListener.class})
public class SubmissionParserClientImpl implements SubmissionParserClient, SubmissionParserListener {

    @Reference protected SubmissionParser submissionParser;
    @Reference protected SubmissionStoreService submissionStoreService;
    @Reference protected InstructionStoreService instructionStoreService;

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
        instructionStoreService.store(instruction);
    }

    @Override
    public void onSubmissionHeader(String messageId, String submissionHeader) {
        this.submission = new Submission(messageId);
        this.submission.setSubmissionHeader(submissionHeader);
        submissionStoreService.store(submission);
    }

}
