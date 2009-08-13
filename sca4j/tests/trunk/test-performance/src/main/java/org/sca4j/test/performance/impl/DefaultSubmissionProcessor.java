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
import java.util.List;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;
import org.sca4j.test.performance.api.BindingListener;
import org.sca4j.test.performance.api.ExceptionStore;
import org.sca4j.test.performance.api.Parser;
import org.sca4j.test.performance.api.PaymentStore;
import org.sca4j.test.performance.api.SubmissionProcessor;
import org.sca4j.test.performance.api.Translator;
import org.sca4j.test.performance.api.ValidationListener;
import org.sca4j.test.performance.api.Validator;

@Conversation
@Service(interfaces = {SubmissionProcessor.class, ValidationListener.class, BindingListener.class})
public class DefaultSubmissionProcessor implements SubmissionProcessor, ValidationListener, BindingListener {
    
    @Reference protected Parser parser;
    @Reference protected Translator translator;
    @Reference protected Validator validator;
    @Reference protected PaymentStore paymentStore;
    @Reference protected ExceptionStore exceptionStore;

    public void close() {
        parser.close();
        translator.close();
        validator.close();
        paymentStore.close();
        exceptionStore.close();
    }

    public void process() {
        Object object = parser.next();
        while (object != null) {
            translator.translate(object);
            object = parser.next();
        }

    }

    public void onResult(Object object, List<URI> failedRules) {
        if (failedRules.isEmpty()) return;
        exceptionStore.store(object, failedRules);
    }

    public void onBind(Object object) {
        paymentStore.store(object);
        validator.validate(object);
        
    }

}
