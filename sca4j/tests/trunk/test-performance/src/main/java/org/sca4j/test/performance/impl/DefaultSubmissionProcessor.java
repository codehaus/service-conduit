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
package org.sca4j.test.performance.impl;

import java.net.URI;
import java.util.List;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
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
@Service(value = {SubmissionProcessor.class, ValidationListener.class, BindingListener.class})
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
