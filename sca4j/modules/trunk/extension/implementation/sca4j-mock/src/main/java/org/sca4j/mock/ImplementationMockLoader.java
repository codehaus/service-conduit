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
package org.sca4j.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * Loads implementation.mock from the scdl. The XML fragment is expeced to look like,
 * <p/>
 * <implementation.mock> org.sca4j.mock.Foo org.sca4j.mock.Bar org.sca4j.mock.Baz </implementation.mock>
 * <p/>
 * The implementation.mock element is expected to have a delimitted list of fully qualified named of the interfaces that need to be mocked.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class ImplementationMockLoader implements TypeLoader<ImplementationMock> {

    private final MockComponentTypeLoader componentTypeLoader;

    /**
     * Initializes the loader registry.
     *
     * @param componentTypeLoader Component type loader.
     */
    @SuppressWarnings("deprecation")
    public ImplementationMockLoader(@Reference MockComponentTypeLoader componentTypeLoader) {
        this.componentTypeLoader = componentTypeLoader;
    }

    /**
     * Loads implementation.mock element from the SCDL.
     *
     * @param reader  StAX reader using which the scdl is loaded.
     * @param context Loader context containing contextual information.
     * @return An instance of mock implementation.
     */
    public ImplementationMock load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {

        assert reader.getName().equals(ImplementationMock.IMPLEMENTATION_MOCK);

        String textualContent = reader.getElementText().trim();

        List<String> mockedInterfaces = new ArrayList<String>();

        StringTokenizer tok = new StringTokenizer(textualContent);
        while (tok.hasMoreElements()) {
            mockedInterfaces.add(tok.nextToken().trim());
        }

        MockComponentType componentType = componentTypeLoader.load(mockedInterfaces, context);

        assert reader.getName().equals(ImplementationMock.IMPLEMENTATION_MOCK);

        return new ImplementationMock(mockedInterfaces, componentType);

    }

}
