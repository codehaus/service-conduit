package com.travelex.tgbp.transform.api.resource;

import java.io.InputStream;

/**
 * An interface describing a resource used for transformation. This interface needs to be implemented by a class in the module
 * where a transformation template (or other resource) is located, to get around class loading issues (a component in F3 may not have
 * access to non-class resources in another jar file).
 */
public interface Resource {

    /**
     * Return the name of this resource.
     * 
     * @return the name
     */
    String getName();

    /**
     * Return the encoding used by this resource (for example UTF-8).
     * 
     * @return the encoding
     */
    String getEncoding();

    /**
     * Return the content of the resource as an {@link InputStream}.
     * 
     * @return the content
     */
    InputStream getContent();

}
