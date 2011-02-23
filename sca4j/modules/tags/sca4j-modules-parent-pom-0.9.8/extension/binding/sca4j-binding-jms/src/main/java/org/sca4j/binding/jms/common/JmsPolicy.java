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
 */
package org.sca4j.binding.jms.common;

/**
 * Base JMS policy which could be requested on the binding.
 * 
 * @see AvailabilityJmsPolicy
 */
public abstract class JmsPolicy {
    
    /**
     * Create availability policy with the given cron expression.
     * @param cronExpression
     * @return {@link AvailabilityJmsPolicy}
     */
    public static AvailabilityJmsPolicy createAvailabilityPolicy(String cronExpression) {
        return new AvailabilityJmsPolicy(cronExpression);
    }
    
    /**
     * Create availability policy with the given repeat interval in ms.
     * @param repeatInterval repeat interval in ms
     * @return {@link AvailabilityJmsPolicy}
     */
    public static AvailabilityJmsPolicy createAvailabilityPolicy(long repeatInterval) {
        return new AvailabilityJmsPolicy(repeatInterval);
    }
    
    /**
     * Resolves given {@link JmsPolicy} to the {@link AvailabilityJmsPolicy}, null is returned if policy couldn't be resolved
     * @param jmsPolicy {@link JmsPolicy}
     * @return {@link AvailabilityJmsPolicy} if given policy is {@link AvailabilityJmsPolicy}, null otherwise.
     */
    public static AvailabilityJmsPolicy resolveAvailabilityPolicy(JmsPolicy jmsPolicy) {
        if (jmsPolicy != null && AvailabilityJmsPolicy.class.isInstance(jmsPolicy)) {
            return AvailabilityJmsPolicy.class.cast(jmsPolicy);
        } else {
            return null;
        }
    }
    
    /**
     * JMS availability policy which defines service availability for message consumption.
     */
    public static class AvailabilityJmsPolicy extends JmsPolicy {
        private String cronExpression;
        private long repeatInterval;

        private AvailabilityJmsPolicy(String cronExpression) {
            this.cronExpression = cronExpression;
        }
        private AvailabilityJmsPolicy(long repeatInterval) {
            this.repeatInterval = repeatInterval;
        }

        /**
         * Cron expression defining polling pattern for the service
         * @return cron expression for the polling
         */
        public String getCronExpression() {
            return cronExpression;
        }

        /**
         * Polling repeat interval for the service.
         * @return time interval polling must be repeated
         */
        public long getRepeatInterval() {
            return repeatInterval;
        }
    }
}
