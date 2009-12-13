package com.travelex.tgbp.output.service;

/**
 * Represents listener service to trigger output.
 */
public interface OutputService {

    /**
     * Initiates payment output.
     *
     * @param message - notification message.
     */
    void start(Object message);
}
