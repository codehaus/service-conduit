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
package org.sca4j.rs.introspection;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.ImplementationProcessor;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.rs.scdl.RsBindingDefinition;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;

/**
 * @version $Rev: 5443 $ $Date: 2008-09-19 15:55:03 +0100 (Fri, 19 Sep 2008) $
 */
@EagerInit
public class RsImplementationLoader implements TypeLoader<JavaImplementation> {

    private final LoaderHelper loaderHelper;
    private final ImplementationProcessor processor;
    private final RsHeuristic rsHeuristic;

    public RsImplementationLoader(@Reference(name = "implementationProcessor") ImplementationProcessor processor,
            @Reference(name = "RsHeuristic") RsHeuristic rsHeuristic,
            @Reference LoaderHelper loaderHelper) {
        this.processor = processor;
        this.loaderHelper = loaderHelper;
        this.rsHeuristic = rsHeuristic;
    }

    public JavaImplementation load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {

        String className = reader.getAttributeValue(null, "class");
        String webApp = reader.getAttributeValue(null, "uri");
        URI webAppURI = null;

        if (className == null) {
            MissingAttribute failure = new MissingAttribute("No class name specified", "class", reader);
            context.addError(failure);
            return null;
        }

        if (webApp == null) {
            MissingAttribute failure = new MissingAttribute("No web application URI specified", "uri", reader);
            context.addError(failure);
            return null;
        }
        try {
            webAppURI = new URI(webApp);
        } catch (URISyntaxException ex) {
            InvalidValue failure = new InvalidValue("invalid URI value", "uri", reader);
            context.addError(failure);
            return null;
        }

        JavaImplementation impl = new JavaImplementation();
        impl.setImplementationClass(className);
        loaderHelper.loadPolicySetsAndIntents(impl, reader, context);
        processor.introspect(impl, context);
        LoaderUtil.skipToEndElement(reader);
        rsHeuristic.applyHeuristics(impl, webAppURI, context);
        return impl;
    }
}
