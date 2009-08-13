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
package org.sca4j.test.performance.impl;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.scope.Conversation;
import org.sca4j.test.performance.api.Rule;
import org.sca4j.test.performance.api.ValidationListener;
import org.sca4j.test.performance.api.Validator;

@Conversation
public class DefaultValidator implements Validator {
    
    @Reference protected List<Rule> rules;
    @Callback protected ValidationListener validationListener;

    public void close() {
        // TODO Auto-generated method stub

    }

    public void validate(Object object) {
        List<URI> failedRules = new LinkedList<URI>();
        for (Rule rule : rules) {
            if (!rule.validate(object)) {
                failedRules.add(rule.getUri());
            }
        }
        validationListener.onResult(object, failedRules);

    }

}
