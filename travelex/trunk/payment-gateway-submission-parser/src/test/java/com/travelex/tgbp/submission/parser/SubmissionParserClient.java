package com.travelex.tgbp.submission.parser;

import java.io.InputStream;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

@Conversational
public interface SubmissionParserClient {

    @EndsConversation
    void parse(InputStream inputData);

}
