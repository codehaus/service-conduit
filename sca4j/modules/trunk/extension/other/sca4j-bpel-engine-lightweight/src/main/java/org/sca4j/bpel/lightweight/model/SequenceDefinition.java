package org.sca4j.bpel.lightweight.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: meerajk
 * Date: May 29, 2010
 * Time: 10:39:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceDefinition {

    private List<AbstractActivity> activities = new ArrayList<AbstractActivity>();

    public List<AbstractActivity> getActivities() {
        return activities;
    }

    public AssignDefinition getLastAssignActivity() {
        return (AssignDefinition) getActivities().get(getActivities().size() - 1);
    }
    
}
