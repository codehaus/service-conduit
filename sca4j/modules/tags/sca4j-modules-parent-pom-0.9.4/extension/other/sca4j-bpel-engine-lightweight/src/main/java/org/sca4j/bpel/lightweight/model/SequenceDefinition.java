/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 */
package org.sca4j.bpel.lightweight.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:39:00 AM
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
