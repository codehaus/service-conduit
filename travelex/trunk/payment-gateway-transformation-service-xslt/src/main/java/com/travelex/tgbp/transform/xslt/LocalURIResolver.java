package com.travelex.tgbp.transform.xslt;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;


/**
 * The Class LocalURIResolver.
 */
class LocalURIResolver implements URIResolver {

    /** The parent resolver. */
    private final URIResolver parentResolver;

    /** The sources. */
    private final Map<String, Source> sources = new HashMap<String, Source>();

    /**
     * Instantiates a new local uri resolver.
     * 
     * @param parentResolver the parent resolver
     */
    public LocalURIResolver(URIResolver parentResolver) {
        this.parentResolver = parentResolver;
    }

    /**
     * Adds the source.
     * 
     * @param name the name
     * @param source the source
     */
    public void addSource(String name, Source source) {
        sources.put(name, source);
    }
    /**
     * {@inheritDoc}
     */
    public Source resolve(String href, String base) throws TransformerException {
        if (!sources.containsKey(base + href)) {
            return parentResolver.resolve(href, base);
        }
        else {
            return sources.get(base + href);
        }
    }
}