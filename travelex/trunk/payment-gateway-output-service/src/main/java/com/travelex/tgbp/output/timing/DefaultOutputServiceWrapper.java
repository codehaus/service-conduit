package com.travelex.tgbp.output.timing;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.output.service.FileOutputService;
import com.travelex.tgbp.output.service.InstructionOutputService;

public class DefaultOutputServiceWrapper implements OutputServiceWrapper {

    @Reference protected InstructionOutputService instructionOutputService;
    @Reference protected FileOutputService fileOutputService;

    @Override
    public void outputFiles(Object message) {
        fileOutputService.outputFiles(message);

    }

    @Override
    public void outputInstructions(Object message) {
        instructionOutputService.outputInstructions(message);
    }

}
