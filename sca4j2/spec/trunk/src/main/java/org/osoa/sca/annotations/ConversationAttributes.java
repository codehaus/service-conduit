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
package org.osoa.sca.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate the characteristics of a conversation.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface ConversationAttributes {
    /**
     * The maximum time that can pass between operations in a single conversation. If this time is exceeded the
     * container may end the conversation.
     *
     * @return the maximum time that can pass between operations in a single conversation
     */
    public String maxIdleTime() default "";

    /**
     * The maximum time that a conversation may remain active. If this time is exceeded the container may end the
     * conversation.
     *
     * @return the maximum time that a conversation may remain active
     */
    public String maxAge() default "";

    /**
     * If true, indicates that only the user that initiated the conversation has the authority to continue it.
     *
     * @return true if only the user that initiated the conversation has the authority to continue it
     */
    public boolean singlePrincipal() default false;
}
