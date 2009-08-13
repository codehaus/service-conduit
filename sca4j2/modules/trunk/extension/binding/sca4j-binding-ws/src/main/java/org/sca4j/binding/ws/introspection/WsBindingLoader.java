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
package org.sca4j.binding.ws.introspection;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ws.scdl.WsBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;

/**
 * @version $Revision: 5465 $ $Date: 2008-09-21 23:12:21 +0100 (Sun, 21 Sep 2008) $
 */
@EagerInit
public class WsBindingLoader implements TypeLoader<WsBindingDefinition> {

    /**
     * Qualified name for the binding element.
     */
    public static final QName BINDING_QNAME = new QName(Constants.SCA_NS, "binding.ws");
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("uri", "uri");
        ATTRIBUTES.put("impl", "impl");
        ATTRIBUTES.put("wsdlElement", "wsdlElement");
        ATTRIBUTES.put("wsdlLocation", "wsdlLocation");
        ATTRIBUTES.put("requires", "requires");
        ATTRIBUTES.put("policySets", "policySets");
    }

    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     *
     * @param loaderHelper the policy helper
     */
    public WsBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public WsBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        validateAttributes(reader, introspectionContext);

        WsBindingDefinition bd = null;
        String uri = null;
        try {

            uri = reader.getAttributeValue(null, "uri");
            String implementation = reader.getAttributeValue(org.sca4j.host.Namespaces.SCA4J_NS, "impl");
            String wsdlElement = reader.getAttributeValue(null, "wsdlElement");
            String wsdlLocation = reader.getAttributeValue("http://www.w3.org/2004/08/wsdl-instance", "wsdlLocation");

            if (uri == null) {
                MissingAttribute failure = new MissingAttribute("The uri attribute is not specified", "uri", reader);
                introspectionContext.addError(failure);
                bd = new WsBindingDefinition(null, implementation, wsdlLocation, wsdlElement, loaderHelper.loadKey(reader));
            } else {
                // encode the URI since there may be expressions (e.g. "${..}") contained in it
                URI endpointUri = new URI(URLEncoder.encode(uri, "UTF-8"));
                bd = new WsBindingDefinition(endpointUri, implementation, wsdlLocation, wsdlElement, loaderHelper.loadKey(reader));
            }
            loaderHelper.loadPolicySetsAndIntents(bd, reader, introspectionContext);
            
            //Load optional config parameters
            loadConfig(bd, reader);

            // TODO Add rest of the WSDL support

        } catch (URISyntaxException ex) {
            InvalidValue failure = new InvalidValue("The web services binding URI is not a valid: " + uri, "uri", reader);
            introspectionContext.addError(failure);
        } catch (UnsupportedEncodingException e) {
            InvalidValue failure = new InvalidValue("Invalid encoding for URI: " + uri + "\n" + e, "uri", reader);
            introspectionContext.addError(failure);
        }

        //LoaderUtil.skipToEndElement(reader);
        return bd;

    }

    private void loadConfig(WsBindingDefinition bd, XMLStreamReader reader) throws XMLStreamException {
	Map<String, String> config = null;
	String name = null;
        while (true) {
            switch(reader.next()) {
                case START_ELEMENT:
                    name = reader.getName().getLocalPart();
                    if("config".equals(name)) {
                	config = new HashMap<String, String>();
                    } else if ("parameter".equals(name)) {
                        final String key = reader.getAttributeValue(null, "name");
                        final String value = reader.getElementText();
                        config.put(key, value);
                    }
                    break;
                case END_ELEMENT:
                    name = reader.getName().getLocalPart();
                    if("config".equals(name)) {
                	bd.setConfig(config);
                    } else if("binding.ws".equals(name)) {
                        return ;
                    }
                    break;
            }
        }        
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.containsKey(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
