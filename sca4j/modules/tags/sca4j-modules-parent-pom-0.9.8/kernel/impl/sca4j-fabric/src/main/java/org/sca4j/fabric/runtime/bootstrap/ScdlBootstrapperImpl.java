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
package org.sca4j.fabric.runtime.bootstrap;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.host.expression.ExpressionExpander;
import org.sca4j.host.expression.ExpressionExpansionException;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.validation.InvalidCompositeException;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.scdl.Composite;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.system.introspection.BootstrapLoaderFactory;
import org.sca4j.system.introspection.SystemImplementationProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Bootstrapper that initializes a runtime by reading a system SCDL file.
 *
 * @version $Rev: 5351 $ $Date: 2008-09-08 21:45:19 +0100 (Mon, 08 Sep 2008) $
 */
public class ScdlBootstrapperImpl extends AbstractBootstrapper {

    private final XMLFactory xmlFactory;

    private URL scdlLocation;

    public ScdlBootstrapperImpl(URL scdlLocation, String systemConfig) {
        this(new XMLFactoryImpl(new ExpressionExpander() {
            public String expand(String value) throws ExpressionExpansionException {
                return value;
            }
        }), systemConfig);
        this.scdlLocation = scdlLocation;
    }

    private ScdlBootstrapperImpl(XMLFactory xmlFactory, String systemConfig) {
        super(systemConfig);
        this.xmlFactory = xmlFactory;
    }

    protected Composite loadSystemComposite(URI contributionUri,
                                            ClassLoader bootClassLoader,
                                            SystemImplementationProcessor processor,
                                            MonitorFactory monitorFactory) throws InitializationException {
        try {
            Loader loader = BootstrapLoaderFactory.createLoader(processor, monitorFactory, xmlFactory);

            // load the system composite
            IntrospectionContext introspectionContext = new DefaultIntrospectionContext(bootClassLoader, contributionUri, scdlLocation);
            Composite composite = loader.load(scdlLocation, Composite.class, introspectionContext);
            composite.validate(introspectionContext);
            if (introspectionContext.hasErrors()) {
                QName name = composite.getName();
                List<ValidationFailure<?>> errors = introspectionContext.getErrors();
                List<ValidationFailure<?>> warnings = introspectionContext.getWarnings();
                throw new InvalidCompositeException(name, errors, warnings);
            }
            return composite;
        } catch (ContributionException e) {
            throw new InitializationException(e);
        } catch (LoaderException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * Creates a default configuration domain property.
     *
     * @return a document representing the configuration domain property
     */
    protected Document createDefaultConfigProperty() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().newDocument();
            Element root = document.createElement("config");
            document.appendChild(root);
            return document;
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }
}
