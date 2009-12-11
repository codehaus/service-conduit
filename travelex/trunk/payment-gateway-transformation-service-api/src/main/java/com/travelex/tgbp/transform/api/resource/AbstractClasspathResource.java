package com.travelex.tgbp.transform.api.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Abstract implementation of a {@link Resource} which loads its content from the classpath. Subclass this class in the module containing
 * the actual resource to be loaded.  See {@link Resource} for more information.
 */
public abstract class AbstractClasspathResource implements Resource {

    private final String name;
    private final String encoding;
    private final byte[] content;

    /**
     * Default constructor.
     * 
     * @param name the name of the resource
     * @param encoding the encoding used by the resource (e.g. UTF-8)
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AbstractClasspathResource(String name, String encoding) throws IOException {
        this.name = name;
        this.encoding = encoding;

        InputStream is = getClass().getResourceAsStream(name);
        this.content = IOUtils.toByteArray(is);
    }


    /**
     * {@inheritDoc}
     */
    public InputStream getContent() {
        return new ByteArrayInputStream(content);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Resource[" + getName() + "," + getEncoding() + "]";
    }
}