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

import java.util.LinkedList;
import java.util.List;


public class IfDefinition extends AbstractActivity {

    private String condition;
    private List<AbstractActivity> actions = new LinkedList<AbstractActivity>();
    private List<IfDefinition> elseIfs = new LinkedList<IfDefinition>();
    private List<AbstractActivity> elseActivities = new LinkedList<AbstractActivity>();

    public List<AbstractActivity> getElseActivities() {
        return elseActivities;
    }

    public List<IfDefinition> getElseIfs() {
        return elseIfs;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<AbstractActivity> getActions() {
        return actions;
    }

    @Override
    public Type getType() {
        return Type.IF;
    }

}
