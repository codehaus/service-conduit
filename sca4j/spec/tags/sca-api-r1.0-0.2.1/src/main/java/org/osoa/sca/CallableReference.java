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
 *
 */
package org.osoa.sca;

/**
 * Common superclass for references that can be passed between components.
 * 
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 * @param <B> the Java interface associated with this reference
 */
public interface CallableReference<B> {
    /**
     * Returns a type-safe reference to the target of this reference.
     * The instance returned is guaranteed to implement the business interface for this reference
     * but may not be a proxy as defined by java.lang.reflect.Proxy.
     *
     * @return a proxy to the target that implements the business interface associated with this reference
     */
    B getService();

    /**
     * Returns the Java class for the business interface associated with this reference.
     *
     * @return the Class for the business interface associated with this reference
     */
    Class<B> getBusinessInterface();

    /**
     * Returns true if this reference is conversational.
     *
     * @return true if this reference is conversational
     */
    boolean isConversational();

    /**
     * Returns the conversation associated with this reference.
     * Returns null if no conversation is currently active.
     *
     * @return the conversation associated with this reference; may be null
     */
    Conversation getConversation();

    /**
     * Returns the callback ID.
     *
     * @return the callback ID
     */
    Object getCallbackID();
}
