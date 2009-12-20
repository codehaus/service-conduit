package com.travelex.tgbp.submission.parser.api;

import java.io.InputStream;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * Input submission parser
 */
@Conversational
@Callback(SubmissionParserListener.class)
public interface SubmissionParser {

    /**
     * Parses given stream of data.
     *
     * @param submissionFile - data to parse.
     * @throws SubmissionParsingException
     */
    void parse(InputStream submissionFile) throws SubmissionParsingException;

    /**
     * Closes current client conversation.
     */
    @EndsConversation
    void close();

}
