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
package org.sca4j.loader.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.introspection.xml.UnrecognizedElement;
import org.sca4j.introspection.xml.UnrecognizedElementException;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ModelObject;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.scdl.OperationDefinition;
import org.sca4j.scdl.ServiceContract;

/**
 * Loads a reference from an XML-based assembly file
 *
 * @version $Rev: 5222 $ $Date: 2008-08-19 18:42:30 +0100 (Tue, 19 Aug 2008) $
 */
public class ComponentReferenceLoader implements TypeLoader<ComponentReference> {
    private static final QName CALLBACK = new QName(SCA_NS, "callback");
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("autowire", "autowire");
        ATTRIBUTES.put("target", "target");
        ATTRIBUTES.put("multiplicity", "multiplicity");
        ATTRIBUTES.put("requires", "requires");
        ATTRIBUTES.put("policySets", "policySets");
    }

    private final Loader loader;
    private final LoaderHelper loaderHelper;

    public ComponentReferenceLoader(@Reference Loader loader, @Reference LoaderHelper loaderHelper) {
        this.loader = loader;
        this.loaderHelper = loaderHelper;
    }

    public ComponentReference load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        String name = reader.getAttributeValue(null, "name");
        if (name == null) {
            MissingReferenceName failure = new MissingReferenceName(reader);
            context.addError(failure);
            return null;
        }
        ComponentReference reference = new ComponentReference(name);

        boolean autowire = Boolean.parseBoolean(reader.getAttributeValue(null, "autowire"));
        reference.setAutowire(autowire);

        String value = reader.getAttributeValue(null, "multiplicity");
        try {
            Multiplicity multiplicity = Multiplicity.fromString(value);
            if (multiplicity != null) {
                reference.setMultiplicity(multiplicity);
            }
        } catch (IllegalArgumentException e) {
            InvalidValue failure = new InvalidValue("Invalid multiplicity value: " + value, value, reader);
            context.addError(failure);
        }

        String target = reader.getAttributeValue(null, "target");
        List<URI> uris = new ArrayList<URI>();
        if (target != null) {
            StringTokenizer tokenizer = new StringTokenizer(target);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                uris.add(loaderHelper.getURI(token));
            }
        }
        reference.getTargets().addAll(uris);

        loaderHelper.loadPolicySetsAndIntents(reference, reader, context);

        boolean callback = false;
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                callback = CALLBACK.equals(reader.getName());
                if (callback) {
                    reader.nextTag();
                }
                QName elementName = reader.getName();
                ModelObject type;
                try {
                    type = loader.load(reader, ModelObject.class, context);
                    // TODO when the loader registry is replaced this try..catch must be replaced with a check for a loader and an
                    // UnrecognizedElement added to the context if none is found
                } catch (UnrecognizedElementException e) {
                    UnrecognizedElement failure = new UnrecognizedElement(reader);
                    context.addError(failure);
                    continue;
                }
                if (type instanceof ServiceContract) {
                    reference.setServiceContract((ServiceContract<?>) type);
                } else if (type instanceof BindingDefinition) {
                    if (callback) {
                        reference.addCallbackBinding((BindingDefinition) type);
                    } else {
                        reference.addBinding((BindingDefinition) type);
                    }
                } else if (type instanceof OperationDefinition) {
                    reference.addOperation((OperationDefinition) type);
                } else if (type == null) {
                    // error loading, the element, ignore as an error will have been reported
                    break;
                } else {
                    context.addError(new UnrecognizedElement(reader));
                    continue;
                }
                if (!reader.getName().equals(elementName) || reader.getEventType() != END_ELEMENT) {
                    throw new AssertionError("Loader must position the cursor to the end element");
                }
                break;
            case END_ELEMENT:
                if (callback) {
                    callback = false;
                    break;
                }
                return reference;
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
