/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without
 * warranties or conditions of any kind.  See the License for the specific language
 * governing permissions and limitations of use of the software. This software is
 * distributed in conjunction with other software licensed under different terms.
 * See the separate licenses for those programs included in the distribution for the
 * permitted and restricted uses of such software.
 */
package org.sca4j.binding.oracle.queue.spi;

/**
 * Abstraction for the queue manager.
 */
public interface AQQueueManager {

    /**
     * Dequeues a message.
     *
     * @param queueName Queue name.
     * @param correlationId Correlation id.
     * @param delay Delay timeout.
     * @param dataSourceKey Data source key.
     * @return {@link QueuePayload}.
     *
     * @throws AQQueueException
     */
    QueuePayload dequeue(String queueName, String correlationId, int delay, String dataSourceKey) throws AQQueueException;

    /**
     * Enqueues a message.
     *
     * @param queueName Queue name.
     * @param correlationId Correlation id.
     * @param delay Enqueue delay.
     * @param message Message to be enqueued.
     * @param dataSourceKey Data source key.
     *
     * @throws AQQueueException
     */
    void enqueue(String queueName, String correlationId, int delay, Object message, String dataSourceKey) throws AQQueueException;

}
