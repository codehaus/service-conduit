package com.travelex.tgbp.submission.parser;

import java.util.List;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;

public interface Store {

    void addSubmission(Submission s);

    void addInstruction(Instruction i);

    List<Submission> getSubmissions();

    List<Instruction> getInstructions();

}
