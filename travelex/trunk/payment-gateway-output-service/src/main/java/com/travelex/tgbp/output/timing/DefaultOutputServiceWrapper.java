package com.travelex.tgbp.output.timing;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.FileOutputService;
import com.travelex.tgbp.output.service.InstructionOutputService;

public class DefaultOutputServiceWrapper implements OutputServiceWrapper {

    @Reference protected InstructionOutputService instructionOutputService;
    @Reference protected FileOutputService fileOutputService;

    private static final String SUCESS_RESPONSE = "Success";

    /**
     * {@inheritDoc}
     */
    @Override
    public String startOutputService(String message) {
        instructionOutputService.outputInstructions(message);
        fileOutputService.outputFiles(message);
        return SUCESS_RESPONSE;
    }

}
