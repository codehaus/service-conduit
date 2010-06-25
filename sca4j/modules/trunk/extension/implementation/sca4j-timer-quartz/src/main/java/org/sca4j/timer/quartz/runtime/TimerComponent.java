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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.timer.quartz.runtime;

import java.net.URI;
import java.text.ParseException;
import java.util.concurrent.ScheduledFuture;

import org.sca4j.java.runtime.JavaComponent;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.timer.quartz.provision.TriggerData;
import org.sca4j.timer.quartz.spi.TimerService;

/**
 * A timer component implementation.
 *
 * @version $Revision$ $Date$
 */
public class TimerComponent<T> extends JavaComponent<T> {
    private TriggerData data;
    private TimerService timerService;
    private ScheduledFuture<?> future;

    /**
     * Constructor for a timer component.
     *
     * @param componentId             the component's uri
     * @param instanceFactoryProvider the provider for the instance factory
     * @param scopeContainer          the container for the component's implementation scope
     * @param groupId                 the component group this component belongs to
     * @param initLevel               the initialization level
     * @param maxIdleTime             the time after which idle instances of this component can be expired
     * @param maxAge                  the time after which instances of this component can be expired
     * @param proxyService            the service used to create reference proxies
     * @param data                    timer fire data
     * @param timerService            the timer service
     */
    public TimerComponent(URI componentId,
                          InstanceFactoryProvider<T> instanceFactoryProvider,
                          ScopeContainer<?> scopeContainer,
                          URI groupId,
                          int initLevel,
                          long maxIdleTime,
                          long maxAge,
                          ProxyService proxyService,
                          TriggerData data,
                          TimerService timerService) {
        super(componentId,
              instanceFactoryProvider,
              scopeContainer,
              groupId,
              initLevel,
              maxIdleTime,
              maxAge,
              proxyService);
        this.data = data;
        this.timerService = timerService;
    }

    public void start() {
        super.start();
        TimerComponentInvoker<T> invoker = new TimerComponentInvoker<T>(this);
        switch (data.getType()) {
        case CRON:
            try {
                future = timerService.schedule(invoker, data.getCronExpression());
            } catch (ParseException e) {
                // this should be caught on the controller
                throw new TimerComponentInitException(e);
            }
            break;
        case FIXED_RATE:
            throw new UnsupportedOperationException("Not yet implemented");
            // break;
        case INTERVAL:
            future = timerService.scheduleWithFixedDelay(invoker, data.getStartTime(), data.getRepeatInterval(), data.getTimeUnit());
            break;
        case ONCE:
            future = timerService.schedule(invoker, data.getFireOnce(), data.getTimeUnit());
            break;
        }
    }

    public void stop() {
        super.stop();
        if (future != null && !future.isCancelled() && !future.isDone()) {
            future.cancel(true);
        }
    }


}
