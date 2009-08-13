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

package org.sca4j.spi.services.archive;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Implementations store artifacts
 */
public interface ArchiveStore {

    /**
     * Copies an artifact to the store.
     *
     * @param uri    A URI pointing to the artifact being copied to the store
     * @param stream InputStream with the content of the distribution
     * @return a URL pointing to the stored artifact
     * @throws ArchiveStoreException if an error occurs storing the artifact
     */
    URL store(URI uri, InputStream stream) throws ArchiveStoreException;

    /**
     * Copy an artifact from the source URL to the store
     *
     * @param contributionUri the URI of the artifact
     * @param sourceURL       the source URL of the artifact
     * @return a URL pointing to the stored artifact
     * @throws ArchiveStoreException if an error occurs storing the artifact
     */
    URL store(URI contributionUri, URL sourceURL) throws ArchiveStoreException;

    /**
     * Look up the artifact URL by URI
     *
     * @param uri The URI of the artifact
     * @return A URL pointing to the artifact or null if the artifact cannot be found
     * @throws ArchiveStoreException if an exception occurs storing the contribution
     */
    URL find(URI uri) throws ArchiveStoreException;

    /**
     * Remove an artifact from the store
     *
     * @param uri The URI of the contribution to be removed
     * @throws ArchiveStoreException if an exception occurs removing the contribution
     */
    void remove(URI uri) throws ArchiveStoreException;

    /**
     * Get list of URIs for all the artifacts in the store
     *
     * @return A list of artifact URIs
     */
    List<URI> list();
}
