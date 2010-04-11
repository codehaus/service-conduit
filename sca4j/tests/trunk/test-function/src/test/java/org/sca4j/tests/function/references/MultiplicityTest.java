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
package org.sca4j.tests.function.references;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import org.oasisopen.sca.annotation.Reference;

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
