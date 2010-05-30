package org.sca4j.bpel.lightweight.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:39:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssignDefinition extends AbstractActivity {

    private List<CopyDefinition> copies = new ArrayList<CopyDefinition>();

    public List<CopyDefinition> getCopies() {
        return copies;
    }

    public CopyDefinition getLastCopy() {
        return getCopies().get(getCopies().size() - 1);
    }

}