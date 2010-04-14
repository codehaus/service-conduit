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
package org.sca4j.tests.function.wiring;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.tests.function.common.HelloService;

/**
 * @version $Rev: 5252 $ $Date: 2008-08-22 02:41:49 +0100 (Fri, 22 Aug 2008) $
 */
public class TestComponent implements TestService {
    private HelloService constructorService;
    private HelloService service;
    private HelloService promotedReference;
    private HelloService nonConfiguredPromotedReference;
    private HelloService optionalNonSetReference;
    private HelloService wireElementReference;

    public TestComponent(@Reference(name = "targetConstructor")HelloService constructorHelloService) {
        this.constructorService = constructorHelloService;
    }

    @Reference
    public void setService(HelloService service) {
        this.service = service;
    }

    @Reference
    public void setPromotedReference(HelloService promotedReference) {
        this.promotedReference = promotedReference;
    }

    @Reference
    public void setNonConfiguredPromotedReference(HelloService target) {
        this.nonConfiguredPromotedReference = target;
    }

    @Reference(required = false)
    public void setOptionalNonSetReference(HelloService optionalNonSetReference) {
        this.optionalNonSetReference = optionalNonSetReference;
    }

    @Reference
     public void setWireElementReference(HelloService wireElementReference) {
        this.wireElementReference = wireElementReference;
    }

    public HelloService getService() {
        return service;
    }

    public HelloService getPromotedReference() {
        return promotedReference;
    }

    public HelloService getNonConfiguredPromotedReference() {
        return nonConfiguredPromotedReference;
    }

    public HelloService getConstructorService() {
        return constructorService;
    }

    public HelloService getOptionalNonSetReference() {
        return optionalNonSetReference;
    }

    public HelloService getWireElementReference() {
        return wireElementReference;
    }
}
