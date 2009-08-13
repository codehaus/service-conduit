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
package org.sca4j.tests.function.references;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.IdentityService;

/**
 * @version $Rev: 1398 $ $Date: 2007-09-27 00:28:56 +0100 (Thu, 27 Sep 2007) $
 */
public class MultiplicityTest extends TestCase {

    @Reference
    public List<IdentityService> listField;
    
    private List<IdentityService> listSetter;
    
    private List<IdentityService> listCdi1;
    
    private List<IdentityService> listCdi2;

    @Reference
    public void setListSetter(List<IdentityService> listSetter) {
        this.listSetter = listSetter;
    }
    
    public MultiplicityTest(@Reference(name="listCdi1") List<IdentityService> listCdi1,
                            @Reference(name="listCdi2") List<IdentityService> listCdi2) {
        this.listCdi1 = listCdi1;
        this.listCdi2 = listCdi2;
    }

    public void testListCdi1() {
        checkContent(listCdi1);
    }

    public void testListCdi2() {
        checkContent(listCdi2);
    }

    public void testListSetter() {
        checkContent(listSetter);
    }

    public void testListField() {
        checkContent(listField);
    }

    private static final Set<String> IDS;

    static {
        IDS = new HashSet<String>(3);
        IDS.add("map.one");
        IDS.add("map.two");
        IDS.add("map.three");
    }

    private void checkContent(Collection<IdentityService> refs) {
        assertEquals(3, refs.size());
        for (IdentityService ref : refs) {
            // Sets dont guarantee insert order
            assertTrue(IDS.contains(ref.getIdentity()));
        }
    }
}
