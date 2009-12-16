package com.travelex.tgbp.submission.parser;

import java.util.ArrayList;
import java.util.List;

import org.sca4j.api.annotation.scope.Composite;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;

@Composite
public class DefaultStore implements Store {

    private final List<Instruction> instructions = new ArrayList<Instruction>();
    private final List<Submission> submissions = new ArrayList<Submission>();

    public void addInstruction(Instruction i) {
        instructions.add(i);
    }

    public void addSubmission(Submission s) {
        submissions.add(s);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

}
