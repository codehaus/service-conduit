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
package org.sca4j.spi.services.contribution;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * The system registry of contribution processors
 *
 * @version $Rev: 4313 $ $Date: 2008-05-24 00:06:47 +0100 (Sat, 24 May 2008) $
 */
public interface ProcessorRegistry {
    /**
     * Register a ContributionProcessor using the content type as the key
     *
     * @param processor the processor to registrer
     */
    void register(ContributionProcessor processor);

    /**
     * Unregister a ContributionProcessor for a content type
     *
     * @param contentType the content
     */
    void unregisterContributionProcessor(String contentType);

    /**
     * Register a ManifestProcessor using the content type as the key
     *
     * @param processor the processor to registrer
     */
    void register(ManifestProcessor processor);

    /**
     * Unregister a ManifestProcessor for a content type
     *
     * @param contentType the content
     */
    void unregisterManifestProcessor(String contentType);

    /**
     * Register a ResourceProcessor using the content type as the key
     *
     * @param processor the processor to registrer
     */
    void register(ResourceProcessor processor);

    /**
     * Unregister a ResourceProcessor for a content type
     *
     * @param contentType the content
     */
    void unregisterResourceProcessor(String contentType);

    /**
     * Dispatches to a {@link ContributionProcessor} to process manifest information in a contribution.
     *
     * @param contribution the contribution
     * @param context      the context to which validation errors and warnings are reported
     * @throws ContributionException if there was a problem processing the manifest
     */
    void processManifest(Contribution contribution, ValidationContext context) throws ContributionException;

    /**
     * Dispatches to a {@link ManifestProcessor} to process a manifest artifact contaned in a contribution.
     *
     * @param manifest    the manifest to update
     * @param contentType the artifact MIME type
     * @param inputStream the input stream for the artifact
     * @param context     the context to which validation errors and warnings are reported
     * @throws ContributionException if there was a problem processing the artifact
     */
    void processManifestArtifact(ContributionManifest manifest, String contentType, InputStream inputStream, ValidationContext context)
            throws ContributionException;

    /**
     * Dispatches to a {@link ContributionProcessor} to index a contribution.
     *
     * @param contribution the contribution to index
     * @param context      the context to which validation errors and warnings are reported
     * @throws ContributionException if there was a problem indexing the contribution
     */
    void indexContribution(Contribution contribution, ValidationContext context) throws ContributionException;

    /**
     * Dispatches to a {@link ResourceProcessor} to index a resource contained in a contribution.
     *
     * @param contribution the cntaining contribution
     * @param contentType  the content type of the resource to process
     * @param url          a dereferenceable URL for the resource
     * @param context      the context to which validation errors and warnings are reported
     * @throws ContributionException if there was a problem indexing the contribution
     */
    void indexResource(Contribution contribution, String contentType, URL url, ValidationContext context) throws ContributionException;

    /**
     * Loads all indexed resources in a contribution.
     *
     * @param contribution The contribution
     * @param context      the context to which validation errors and warnings are reported
     * @param loader       the classloader conribution resources must be laoded in
     * @throws ContributionException if there was a problem loading resources in the contribution
     */
    void processContribution(Contribution contribution, ValidationContext context, ClassLoader loader) throws ContributionException;

    /**
     * Loads a contained resource in a contribution.
     *
     * @param contributionUri the URI of the active contribution
     * @param resource        the resource to process
     * @param context         the context to which validation errors and warnings are reported
     * @param loader          the classloader contribution the resource must be loaded in
     * @throws ContributionException if there was a problem loading the resoure
     */
    void processResource(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException;

}
