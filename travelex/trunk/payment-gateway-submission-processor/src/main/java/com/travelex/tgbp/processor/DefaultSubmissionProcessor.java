package com.travelex.tgbp.processor;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.InstructionStoreService;
import com.travelex.tgbp.submission.parser.api.SubmissionParser;
import com.travelex.tgbp.submission.parser.api.SubmissionParserListener;

/**
 * Default implementation for {@link SubmissionProcessor}.
 */
@Conversation
@Service(interfaces = {SubmissionProcessor.class, SubmissionParserListener.class})
public class DefaultSubmissionProcessor implements SubmissionProcessor, SubmissionParserListener {

    @Reference protected SubmissionParser submissionParser;
    @Reference protected InstructionStoreService instructionStoreService;

    private Submission submission;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubmission(Submission submission) {
        try {
            this.submission = submission;
            submissionParser.parse(submission.getDataAsStream());
        } finally {
            submissionParser.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInstruction(Instruction instruction) {
        instruction.setSubmissionId(submission.getId());
        instructionStoreService.store(instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubmissionHeader(String messageId) {
        submission.setMessageId(messageId);
    }

}
