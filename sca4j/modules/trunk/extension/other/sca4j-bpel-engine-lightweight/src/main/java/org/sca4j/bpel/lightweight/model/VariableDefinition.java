package org.sca4j.bpel.lightweight.model;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:38:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariableDefinition {

    private String name;
    private QName type;

    public VariableDefinition(String name, QName type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public QName getType() {
        return type;
    }

}
