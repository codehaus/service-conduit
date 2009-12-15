package com.travelex.tgbp.output.service;

/**
 * Represents serviceListener service to trigger output processes.
 */
public interface OutputService {

    /**
     * Triggers output instruction creation process.
     *
     * @param message - notification message.
     */
    void outputInstructions(Object message);

    /**
     * Triggers output submission and file creation process.
     *
     * @param message - notification message.
     */
    void outputFiles(Object message);
}
