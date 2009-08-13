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
package org.sca4j.fabric.runtime.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.fabric.services.documentloader.DocumentLoaderImpl;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Bootstrapper that initializes a runtime by reading a system SCDL file.
 *
 * @version $Rev: 5351 $ $Date: 2008-09-08 21:45:19 +0100 (Mon, 08 Sep 2008) $
 */
public class ScdlBootstrapperImpl extends AbstractBootstrapper {

    private static final String USER_CONFIG = System.getProperty("user.home") + "/.sca4j/config.xml";

    private final XMLFactory xmlFactory;
    private final DocumentLoader documentLoader;

    private URL scdlLocation;
    private URL systemConfig;
    private InputSource systemConfigDocument;

    public ScdlBootstrapperImpl(URL scdlLocation, URL systemConfig, InputSource systemConfigDocument) {
        this(new XMLFactoryImpl(new ExpressionExpander() {
            public String expand(String value) throws ExpressionExpansionException {
                return value;
            }
        }));
        this.scdlLocation = scdlLocation;
        this.systemConfig = systemConfig;
        this.systemConfigDocument = systemConfigDocument;
    }

    private ScdlBootstrapperImpl(XMLFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
        this.documentLoader = new DocumentLoaderImpl();
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
                List<ValidationFailure> errors = introspectionContext.getErrors();
                List<ValidationFailure> warnings = introspectionContext.getWarnings();
                throw new InvalidCompositeException(name, errors, warnings);
            }
            return composite;
        } catch (ContributionException e) {
            throw new InitializationException(e);
        } catch (LoaderException e) {
            throw new InitializationException(e);
        }
    }

    protected Document loadUserConfig() throws InitializationException {
        // Get the user config location
        File configFile = new File(USER_CONFIG);
        if (!configFile.exists()) {
            // none found, create a default one
            return createDefaultConfigProperty();
        }
        try {
            return documentLoader.load(configFile);
        } catch (IOException e) {
            throw new InitializationException(e);
        } catch (SAXException e) {
            throw new InitializationException(e);
        }
    }


    protected Document loadSystemConfig() throws InitializationException {
        if (systemConfigDocument != null) {
            try {
                // load from an external URL
                return documentLoader.load(systemConfigDocument);
            } catch (IOException e) {
                throw new InitializationException(e);
            } catch (SAXException e) {
                throw new InitializationException(e);
            }
        }
        if (systemConfig == null) {
            // none specified, create a default one
            return createDefaultConfigProperty();
        }
        try {
            // load from an external URL
            return documentLoader.load(systemConfig);
        } catch (IOException e) {
            throw new InitializationException(e);
        } catch (SAXException e) {
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
