package org.sca4j.bpel.lightweight.model;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 12:10:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractActivity {
    
    public enum Type {
        RECEIVE, REPLY, INVOKE, ASSIGN;
    }
    
    public abstract Type getType();
}
