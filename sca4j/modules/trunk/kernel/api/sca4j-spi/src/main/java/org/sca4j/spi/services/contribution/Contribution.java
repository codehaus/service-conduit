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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.spi.services.contribution;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The base representation of a deployed contribution
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class Contribution implements Serializable {
    private static final long serialVersionUID = 2511879480122631196L;
    private final URI uri;
    private URL location;
    private byte[] checksum;
    private long timestamp;
    private String contentType;
    private ContributionManifest manifest;
    private List<Resource> resources = new ArrayList<Resource>();
    private List<URI> resolvedImports = new ArrayList<URI>();

    public Contribution(URI uri) {
        this.uri = uri;
    }

    /**
     * Instantiates a new Contribution instance
     *
     * @param uri         the contribution URI
     * @param location    a dereferenceble URL for the contribution archive
     * @param checksum    the checksum for the contribution artifact
     * @param timestamp   the time stamp of the contribution artifact
     * @param contentType the MIME type of the contribution
     */
    public Contribution(URI uri, URL location, byte[] checksum, long timestamp, String contentType) {
        this.uri = uri;
        this.location = location;
        this.checksum = checksum;
        this.timestamp = timestamp;
        this.contentType = contentType;
    }

    /**
     * Returns the contribution URI.
     *
     * @return the contribution URI
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Returns the locally dereferenceable URL for the contribution artifact.
     *
     * @return the dereferenceable URL for the contribution artifact
     */
    public URL getLocation() {
        return location;
    }

    /**
     * Returns the MIME type for the contribution.
     *
     * @return the MIME type for the contribution
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the contribution artifact checksum.
     *
     * @return the contribution artifact checksum
     */
    public byte[] getChecksum() {
        return checksum;
    }

    /**
     * Returns the timestamp of the most recent update to the artifact.
     *
     * @return the timestamp of the most recent update to the artifact
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the contribution manifest
     *
     * @return the contribution manifest
     */
    public ContributionManifest getManifest() {
        return manifest;
    }

    /**
     * Sets the contribution manifest.
     *
     * @param manifest the contribution manifest
     */
    public void setManifest(ContributionManifest manifest) {
        this.manifest = manifest;
    }

    /**
     * Adds a resource to the contribution
     *
     * @param resource the resource
     */
    public void addResource(Resource resource) {
        resources.add(resource);
    }

    /**
     * Returns the list of resources for the contribution.
     *
     * @return the list of resources
     */
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * Returns a ResourceElement matching the symbol or null if not found
     *
     * @param symbol the symbol to match
     * @return a ResourceElement matching the symbol or null if not found
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Symbol> ResourceElement<T, Serializable> findResourceElement(Symbol<T> symbol) {
        for (Resource resource : resources) {
            for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                if (element.getSymbol().equals(symbol)) {
                    return (ResourceElement<T, Serializable>) element;
                }
            }
        }
        return null;
    }

    /**
     * Adds a contribution URI for a resolved import.
     *
     * @param uri the contribution URI
     */
    public void addResolvedImportUri(URI uri) {
        resolvedImports.add(uri);
    }

    /**
     * Returns the list of contribution URIs matching resolved imports.
     *
     * @return the list of contribution URIs matching resolved imports
     */
    public List<URI> getResolvedImportUris() {
        return resolvedImports;
    }

}
