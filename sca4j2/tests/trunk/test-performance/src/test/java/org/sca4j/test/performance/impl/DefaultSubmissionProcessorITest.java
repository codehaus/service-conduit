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

import org.osoa.sca.annotations.Reference;
import org.sca4j.test.performance.api.Rule;
import org.sca4j.test.performance.api.SubmissionProcessor;

import junit.framework.TestCase;

public class DefaultSubmissionProcessorITest extends TestCase {
    
    @Reference protected SubmissionProcessor submissionProcessor;

    public void testByReflection() {
        long now = System.currentTimeMillis();
        submissionProcessor.process();
        submissionProcessor.close();
        System.err.println("By reflection:" + (System.currentTimeMillis() - now));
    }

    public void testByReference() {
        long now = System.currentTimeMillis();
        
        DefaultSubmissionProcessor submissionProcessor = new DefaultSubmissionProcessor();
        DefaultParser parser = new DefaultParser();
        parser.count = 100000;
        DefaultExceptionStore exceptionStore = new DefaultExceptionStore();
        DefaultPaymentStore paymentStore = new DefaultPaymentStore();
        DefaultTranslator translator = new DefaultTranslator();
        DefaultValidator validator = new DefaultValidator();
        
        submissionProcessor.exceptionStore = exceptionStore;
        submissionProcessor.parser = parser;
        submissionProcessor.paymentStore = paymentStore;
        submissionProcessor.translator = translator;
        submissionProcessor.validator = validator;
        
        validator.validationListener = submissionProcessor;
        translator.bindingListener = submissionProcessor;
        
        validator.rules = new LinkedList<Rule>();
        for (int i = 0; i < 10; i++) {
            validator.rules.add(new DefaultRule(URI.create("Rule" + i)));
        }
        
        submissionProcessor.process();
        submissionProcessor.close();
        System.err.println("By reflection:" + (System.currentTimeMillis() - now));
    }

}
