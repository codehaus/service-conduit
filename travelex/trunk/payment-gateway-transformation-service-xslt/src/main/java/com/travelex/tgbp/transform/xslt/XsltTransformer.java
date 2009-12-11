package com.travelex.tgbp.transform.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 * Abstract superclass for xslt transformers.
 */
public abstract class XsltTransformer {

    /** The transformer factory. */
    private TransformerFactory transformerFactory;

    /** The templates. */
    private Templates templates;

    /** The template source. */
    private Source templateSource;

    /** Inject the name of the transformer factory class to use for transformations. */
    @Property(name = "transformerFactory")
    protected String transformerFactoryName;

    @Property
    protected String xsl;

    @Property
    protected String encoding;


    /**
     * Initialises the service.
     * 
     * @throws InstantiationException the instantiation exception
     * @throws ClassNotFoundException the class not found exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TransformerConfigurationException the transformer configuration exception
     */
    @Init
    public void init() throws InstantiationException, ClassNotFoundException, IllegalAccessException, IOException, TransformerConfigurationException {
        if (transformerFactoryName == null || transformerFactoryName.equals("")) {
            transformerFactory = TransformerFactory.newInstance();
        }
        else {
            Class transformerFactoryClass = Class.forName(transformerFactoryName);
            transformerFactory = (TransformerFactory) transformerFactoryClass.newInstance();
        }

        if (!transformerFactory.getFeature(SAXTransformerFactory.FEATURE)) {
            throw new IllegalStateException("Transformer factory doesn't support SAX");
        }

        // URI resolver to pick up imported stylesheets
        transformerFactory.setURIResolver(new URIResolver() {
            public Source resolve(String href, String base) throws TransformerException {
                InputStream resource;
                if (base == null || "".equals(base)) {
                    resource = getClass().getResourceAsStream(href);
                }
                else {
                    try {
                        URL url = new URL(base + href);
                        resource = url.openStream();
                    }
                    catch (MalformedURLException mue) {
                        throw new TransformerException("invalid absolute reference for " + base + href);
                    }
                    catch (IOException ioe) {
                        throw new TransformerException("unable to load absolute reference " + base + href);
                    }
                }

                return new StreamSource(resource);
            }
        });

        InputStream resource = getClass().getResourceAsStream(xsl);
        templateSource = new StreamSource(resource);

        templates = transformerFactory.newTemplates(templateSource);
    }



    /**
     * Gets the configured transformer.
     * 
     * @param xmlSources the xml sources
     * @param context the context
     * 
     * @return the configured transformer
     * 
     * @throws TransformerConfigurationException the transformer configuration exception
     */
    Transformer getConfiguredTransformer(Source[] xmlSources, Map<String, Object> context) throws TransformerConfigurationException {
        Transformer transformer = getTemplates().newTransformer();

        for (Entry<String, Object> entry : context.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }

        if (xmlSources.length > 1) {
            LocalURIResolver uriResolver = new LocalURIResolver(transformer.getURIResolver());
            for (int i = 1; i < xmlSources.length; i++) {
                uriResolver.addSource("document" + i, xmlSources[i]);
            }
            transformer.setURIResolver(uriResolver);
        }

        return transformer;
    }

    /**
     * Gets the templates.
     * 
     * @return the templates
     */
    Templates getTemplates() {
        return templates;
    }
}