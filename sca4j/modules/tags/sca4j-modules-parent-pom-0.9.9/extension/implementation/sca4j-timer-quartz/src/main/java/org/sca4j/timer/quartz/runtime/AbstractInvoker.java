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
package org.sca4j.timer.quartz.runtime;

import java.net.URI;

import org.sca4j.host.management.ManagedAttribute;
import org.sca4j.host.management.ManagementService;
import org.sca4j.host.management.ManagementUnit;

public class AbstractInvoker {
    
    private boolean started = true;
    
    public AbstractInvoker(String componentId, ManagementService managementService) {
        if (managementService != null) {
            managementService.register(URI.create("/implementation.timer/" + componentId), new ManagementUnitImpl());
        }
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public class ManagementUnitImpl implements ManagementUnit {
        
        @ManagedAttribute("Current active state of the component")
        public boolean isStarted() {
            return started;
        }
        
        public void setStarted(boolean started) {
            AbstractInvoker.this.started = started;
        }
        
        @Override
        public String getDescription() {
            return "Timer component";
        }
        
    }

}
