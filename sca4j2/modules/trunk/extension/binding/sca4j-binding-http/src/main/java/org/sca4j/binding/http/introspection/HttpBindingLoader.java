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
package org.sca4j.binding.http.introspection;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.scdl.HttpBindingDefinition;
import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;

public class HttpBindingLoader implements TypeLoader<HttpBindingDefinition> {

    /**
     * Qualified name for the binding element.
     */
    public static final QName BINDING_QNAME = new QName(Namespaces.SCA4J_NS, "binding.http");

    private final LoaderHelper loaderHelper;


    /**
     * Constructor.
     *
     * @param loaderHelper the policy helper
     */
    public HttpBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public HttpBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        validateAttributes(reader, introspectionContext);

        HttpBindingDefinition bd = null;
        String uri = null;
        try {

            uri = reader.getAttributeValue(null, "uri");
            if (uri != null) {
                bd = new HttpBindingDefinition(new URI(uri), loaderHelper.loadKey(reader));
            } else {
                bd = new HttpBindingDefinition(null, loaderHelper.loadKey(reader));
            }

            loaderHelper.loadPolicySetsAndIntents(bd, reader, introspectionContext);

        } catch (URISyntaxException ex) {
            InvalidValue failure = new InvalidValue("The http binding URI is not valid: " + uri, "uri", reader);
            introspectionContext.addError(failure);
        }

        LoaderUtil.skipToEndElement(reader);
        return bd;

    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"uri".equals(name) && !"requires".equals(name) && !"policySets".equals(name) && !"key".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }
    
}
