package org.sca4j.bpel.lightweight.model;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:39:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvokeDefinition extends AbstractActivity {

    private String operation;
    private String partnerLink;
    private String input;
    private String output;

    public InvokeDefinition(String operation, String partnerLink, String input, String output) {
        this.operation = operation;
        this.partnerLink = partnerLink;
        this.input = input;
        this.output = output;
    }

    public String getOperation() {
        return operation;
    }

    public String getPartnerLink() {
        return partnerLink;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
