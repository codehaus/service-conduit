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
package org.sca4j.fabric.instantiator.promotion;

import java.net.URI;

import junit.framework.TestCase;

import org.sca4j.fabric.instantiator.AmbiguousReference;
import org.sca4j.fabric.instantiator.AmbiguousService;
import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.instantiator.NoServiceOnComponent;
import org.sca4j.fabric.instantiator.PromotedComponentNotFound;
import org.sca4j.fabric.instantiator.ReferenceNotFound;
import org.sca4j.fabric.instantiator.ServiceNotFound;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.system.scdl.SystemImplementation;

public class DefaultTargetPromotionServiceTestCase extends TestCase {

    private PromotionResolutionService promotionResolutionService = new DefaultPromotionResolutionService();
    private LogicalCompositeComponent domain = new LogicalCompositeComponent(URI.create("sca4j://runtime"), URI.create("runtime"), null, null);

    public void testNoComponentForPromotedService() {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component#service"));

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
        assertTrue(change.getErrors().get(0) instanceof PromotedComponentNotFound);
    }

    public void testMultipleServicesWithNoServiceFragment() throws Exception {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addService(new LogicalService(URI.create("component#service1"), null, domain));
        logicalComponent.addService(new LogicalService(URI.create("component#service2"), null, domain));

        domain.addComponent(logicalComponent);
        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
        assertTrue(change.getErrors().get(0) instanceof AmbiguousService);
    }

    public void testNoServiceWithNoServiceFragment() throws Exception {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);

        domain.addComponent(logicalComponent);
        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
        assertTrue(change.getErrors().get(0) instanceof NoServiceOnComponent);
    }

    public void testNoServiceWithServiceFragment() throws Exception {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component#service"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);

        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
        assertTrue(change.getErrors().get(0) instanceof ServiceNotFound);
    }

    public void testNoServiceFragment() {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addService(new LogicalService(URI.create("component#service1"), null, domain));
        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
        assertEquals(URI.create("component#service1"), logicalService.getPromotedUri());

    }

    public void testWithServiceFragment() {

        LogicalService logicalService = new LogicalService(URI.create("service"), null, domain);
        logicalService.setPromotedUri(URI.create("component#service1"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addService(new LogicalService(URI.create("component#service1"), null, domain));
        domain.addComponent(logicalComponent);
        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalService, change);
    }

    public void testNoComponentForPromotedReference() {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component#service"));

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
        assertTrue(change.getErrors().get(0) instanceof PromotedComponentNotFound);

    }

    public void testMultipleReferencesWithNoReferenceFragment() throws Exception {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addReference(new LogicalReference(URI.create("component#reference1"), null, domain));
        logicalComponent.addReference(new LogicalReference(URI.create("component#reference2"), null, domain));

        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
        assertTrue(change.getErrors().get(0) instanceof AmbiguousReference);

    }

    public void testNoReferenceWithNoReferenceFragment() {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);


        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
        assert (change.getErrors().get(0) instanceof ReferenceNotFound);
    }

    public void testNoReferenceWithReferenceFragment() {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component#reference"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);


        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
        assertTrue(change.getErrors().get(0) instanceof ReferenceNotFound);
    }

    public void testNoReferenceFragment() {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addReference(new LogicalReference(URI.create("component#reference1"), null, domain));
        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
        assertEquals(URI.create("component#reference1"), logicalReference.getPromotedUris().iterator().next());

    }

    public void testWithReferenceFragment() {

        LogicalReference logicalReference = new LogicalReference(URI.create("reference"), null, domain);
        logicalReference.addPromotedUri(URI.create("component#reference1"));

        LogicalComponent<SystemImplementation> logicalComponent = new LogicalComponent<SystemImplementation>(URI.create("component"),
                                                                                                             URI.create("runtime"),
                                                                                                             null,
                                                                                                             domain);
        logicalComponent.addReference(new LogicalReference(URI.create("component#reference1"), null, domain));
        domain.addComponent(logicalComponent);

        LogicalChange change = new LogicalChange(domain);
        promotionResolutionService.resolve(logicalReference, change);
    }

}
