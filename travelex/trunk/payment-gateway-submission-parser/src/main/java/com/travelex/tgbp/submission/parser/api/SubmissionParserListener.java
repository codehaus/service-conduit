package com.travelex.tgbp.submission.parser.api;

import org.osoa.sca.annotations.Conversational;

import com.travelex.tgbp.store.domain.Instruction;

/**
 * Call back interface for {@link SubmissionParser}.
 */
@Conversational
public interface SubmissionParserListener {

    /**
     * Notifies parsing of submission header.
     *
     * @param messageId - input submission message id.
     */
    void onSubmissionHeader(String messageId);

    /**
     * Notifies parsing and realization of instruction object.
     *
     * @param instruction - realized instruction object.
     */
    void onInstruction(Instruction instruction);

}
