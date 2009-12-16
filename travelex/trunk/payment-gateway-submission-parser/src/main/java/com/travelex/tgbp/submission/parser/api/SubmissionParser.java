package com.travelex.tgbp.submission.parser.api;

import java.io.InputStream;

public interface SubmissionParser {

    void parse(InputStream submissionFile) throws SubmissionParsingException;

}
