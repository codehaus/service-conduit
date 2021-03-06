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
import java.util.UUID;

/**
 * The base representation of a deployed contribution
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class Contribution implements Serializable {
    
    private final URI uri;
    private URL location;
    private long timestamp;
    private String type;
    private ContributionManifest manifest;
    private List<Resource> resources = new ArrayList<Resource>();
    private List<URI> resolvedImports = new ArrayList<URI>();

    public Contribution(URI uri) {
        this.uri = uri;
    }
    
    public Contribution(URL location, long timestamp, String type) {
        this.uri = URI.create("contribution://" + "/" + UUID.randomUUID());
        this.location = location;
        this.timestamp = timestamp;
        this.type = type;
    }

    public URI getUri() {
        return uri;
    }

    public URL getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ContributionManifest getManifest() {
        return manifest;
    }

    public void setManifest(ContributionManifest manifest) {
        this.manifest = manifest;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public List<Resource> getResources() {
        return resources;
    }

    public <RE extends ResourceElement<?>, S> RE findResourceElement(S symbol, Class<RE> resourceElementType) {
        for (Resource resource : resources) {
            for (ResourceElement<?> element : resource.getResourceElements(resourceElementType)) {
                if (element.getSymbol().equals(symbol)) {
                    return resourceElementType.cast(element);
                }
            }
        }
        return null;
    }

    public void addResolvedImportUri(URI uri) {
        resolvedImports.add(uri);
    }

    public List<URI> getResolvedImportUris() {
        return resolvedImports;
    }

}
