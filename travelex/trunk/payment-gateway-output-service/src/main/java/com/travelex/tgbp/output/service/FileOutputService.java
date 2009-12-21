package com.travelex.tgbp.output.service;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * Trigger service to create output submission files.
 */
@Conversational
public interface FileOutputService {

    /**
     * Triggers output submission and file creation process.
     *
     * @param message - notification message.
     */
    @EndsConversation
    void outputFiles(Object message);

}
