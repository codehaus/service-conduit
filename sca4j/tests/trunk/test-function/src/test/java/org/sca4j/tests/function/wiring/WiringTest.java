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

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

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
