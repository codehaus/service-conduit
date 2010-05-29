package org.sca4j.bpel.lightweight.model;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: meerajk
 * Date: May 29, 2010
 * Time: 10:42:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerLinkDefinition {

    private String name;
    private QName type;
    private String myRole;
    private String partnerRole;

    public PartnerLinkDefinition(String name, QName type, String myRole, String partnerRole) {
        this.name = name;
        this.type = type;
        this.myRole = myRole;
        this.partnerRole = partnerRole;
    }

    public String getName() {
        return name;
    }

    public QName getType() {
        return type;
    }

    public String getMyRole() {
        return myRole;
    }

    public String getPartnerRole() {
        return partnerRole;
    }
    
}
