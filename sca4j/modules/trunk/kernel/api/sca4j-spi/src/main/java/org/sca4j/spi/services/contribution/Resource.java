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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.spi.services.contribution;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

/**
 * Represents a resource in a contribution such as a WSDL file or Composite definition
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class Resource implements Serializable {
    private static final long serialVersionUID = 4291622973495594302L;
    private List<ResourceElement<?, ?>> elements = new ArrayList<ResourceElement<?, ?>>();
    private URL url;
    private String contentType;
    private boolean processed;

    public Resource(URL url, String contentType) {
        this.url = url;
        this.contentType = contentType;
    }

    /**
     * Returns the resource content type
     *
     * @return the resource content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns a derefereceable URL to the resource.
     *
     * @return a derefereceable URL to the resource
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Adds a resource element.
     *
     * @param element the resourceElement
     */
    public void addResourceElement(ResourceElement<?, ?> element) {
        elements.add(element);
    }

    public void addResourceElements(Collection<ResourceElement<?, ?>> elements) {
        elements.addAll(elements);
    }

    /**
     * Returns a map of resource elements keyed by their symbol.
     *
     * @return the map of resource elements
     */
    public List<ResourceElement<?, ?>> getResourceElements() {
        return elements;
    }

    /**
     * Returns true if the resource has been fully processed.
     *
     * @return true if the resource has been fully processed
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets if the resource has been processed.
     *
     * @param processed if the resource has been processed
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
