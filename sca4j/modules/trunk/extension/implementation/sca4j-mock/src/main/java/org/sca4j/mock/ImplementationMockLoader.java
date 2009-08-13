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
