package org.sca4j.bpel.lightweight.model;


/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:39:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveDefinition extends AbstractActivity {

    private String operation;
    private String partnerLink;
    private String variable;

    public ReceiveDefinition(String operation, String partnerLink, String variable) {
        this.operation = operation;
        this.partnerLink = partnerLink;
        this.variable = variable;
    }

    public String getOperation() {
        return operation;
    }

    public String getPartnerLink() {
        return partnerLink;
    }

    public String getVariable() {
        return variable;
    }
    
    public Type getType() {
        return Type.RECEIVE;
    }

}
