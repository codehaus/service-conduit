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

import junit.framework.TestCase;
import org.oasisopen.sca.annotation.Reference;

/**
 * @version $Rev: 1433 $ $Date: 2007-10-01 23:58:55 +0100 (Mon, 01 Oct 2007) $
 */
public class WiringTest extends TestCase {
    private TestService service;

    @Reference
    public void setTestService(TestService service) {
        this.service = service;
    }

    /**
     * Tests a wire that is explicitly targeted with a "target="  on a reference
     */
    public void testTargetedWire() {
        assertNotNull(service.getService());
    }

    /**
     * Tests a wire that is explicitly targeted with a "target=" on a constructor
     */
    public void testTargetedConstructorWire() {
        assertNotNull(service.getConstructorService());
    }

    /**
     * Tests a reference configured on the component without a 'target=' and promoted on the composite:
     * <pre>
     *      <component name="TestComponent">
     *          ...
     *          <reference name="promotedReference"/>
     *      </component>
     * <p/>
     *      <reference name="promotedReference" promote="TestComponent/promotedReference">...
     * <pre>
     */
    public void testPromotedReferences() {
        assertNotNull(service.getPromotedReference());
    }

    /**
     * Tests a reference configured solely via promotion:
     * <pre>
     *      <component name="TestComponent">
     *          ...
     *          <!-- no <reference name="promotedReference" -->
     *      </component>
     * <p/>
     *      <reference name="promotedReference" promote="TestComponent/promotedReference">...
     * <pre>
     */
    public void testNonConfiguredPromotedReferences() {
        assertNotNull(service.getNonConfiguredPromotedReference());
    }

    /**
     * Verifies a reference of multiplicity 0..n does not need to be configured
     */
    public void testOptionalNonSetReference() {
        assertNull(service.getOptionalNonSetReference());
    }
}
