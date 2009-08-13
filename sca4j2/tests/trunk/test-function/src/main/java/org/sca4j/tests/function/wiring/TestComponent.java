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
package org.sca4j.tests.function.wiring;

import org.osoa.sca.annotations.Reference;

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
