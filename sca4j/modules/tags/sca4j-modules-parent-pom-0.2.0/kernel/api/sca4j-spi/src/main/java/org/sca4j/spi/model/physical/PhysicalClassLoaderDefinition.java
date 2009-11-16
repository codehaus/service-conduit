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
package org.sca4j.spi.model.physical;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A definition used to provision classloaders on a runtime.
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class PhysicalClassLoaderDefinition {
    private URI uri;
    private List<URI> parentClassLoaders = new ArrayList<URI>();
    private Set<URI> contributionUris = new LinkedHashSet<URI>();
    private Set<URI> extensionUris = new LinkedHashSet<URI>();

    public PhysicalClassLoaderDefinition(URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the classloader uri.
     *
     * @return the classloader uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Associates the classloader with a contribution. When a classloader is created, a copy of the contribution will be avilable on the classpath.
     *
     * @param uri the URI to add
     */
    public void addContributionUri(URI uri) {
        contributionUris.add(uri);
    }

    /**
     * Returns the URIs of contributions associated with this classloader as an ordered Set. Order is guaranteed for set iteration.
     *
     * @return the URIs as an ordered Set
     */
    public Set<URI> getContributionUris() {
        return contributionUris;
    }

    /**
     * Associates the classloader with an extension. When a classloader is created, the extension classes will be visible to the classloader.
     *
     * @param uri the URI to add
     */
    public void addExtensionUri(URI uri) {
        extensionUris.add(uri);
    }

    /**
     * Returns the URIs of extensions associated with this classloader as an ordered Set. Order is guaranteed for set iteration.
     *
     * @return the URIs as an ordered Set
     */
    public Set<URI> getExtensionUris() {
        return extensionUris;
    }

    /**
     * Returns the list of parent classloader URIs.
     *
     * @return the list of parent classloader URIs
     */
    public List<URI> getParentClassLoaders() {
        return parentClassLoaders;
    }

    /**
     * Adds a parent classloader URI.
     *
     * @param uri the classloader URI
     */
    public void addParentClassLoader(URI uri) {
        parentClassLoaders.add(uri);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || obj.getClass() != PhysicalClassLoaderDefinition.class) {
            return false;
        }

        PhysicalClassLoaderDefinition other = (PhysicalClassLoaderDefinition) obj;

        return parentClassLoaders.equals(other.parentClassLoaders)
                && contributionUris.equals(other.contributionUris)
                && extensionUris.equals(other.extensionUris)
                && uri.equals(other.uri);
    }

    public int hashCode() {
        int result;
        result = (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (parentClassLoaders != null ? parentClassLoaders.hashCode() : 0);
        result = 31 * result + (contributionUris != null ? contributionUris.hashCode() : 0);
        result = 31 * result + (extensionUris != null ? extensionUris.hashCode() : 0);
        return result;
    }
}
