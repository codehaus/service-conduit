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
package org.sca4j.itest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.sca4j.featureset.FeatureSet;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.FileContributionSource;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.ScdlBootstrapper;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;

/**
 * @version $Revision$ $Date$
 */
public class ExtensionHelper {

    public ArtifactHelper artifactHelper;

    public void processExtensions(BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> configuration,
                                  Dependency[] extensions,
                                  List<FeatureSet> featureSets) throws MojoExecutionException {
        List<URL> extensionUrls = resolveDependencies(extensions);

        if (featureSets != null) {
            for (FeatureSet featureSet : featureSets) {
                extensionUrls.addAll(processFeatures(featureSet));
            }
        }
        List<ContributionSource> sources = createContributionSources(extensionUrls);
        configuration.setExtensions(sources);
    }

    private List<ContributionSource> createContributionSources(List<URL> urls) {
        List<ContributionSource> sources = new ArrayList<ContributionSource>();
        for (URL extensionUrl : urls) {
            // it's ok to assume archives are uniquely named since most server environments have a single deploy directory
            URI uri = URI.create(new File(extensionUrl.getFile()).getName());
            ContributionSource source = new FileContributionSource(uri, extensionUrl, -1, new byte[0]);
            sources.add(source);
        }
        return sources;
    }

    private List<URL> processFeatures(FeatureSet featureSet) throws MojoExecutionException {
        Set<Dependency> dependencies = featureSet.getExtensions();
        return resolveDependencies(featureSet.getExtensions().toArray(new Dependency[dependencies.size()]));
    }

    private List<URL> resolveDependencies(Dependency[] dependencies) throws MojoExecutionException {

        List<URL> urls = new ArrayList<URL>();

        if (dependencies == null) {
            return urls;
        }

        for (Dependency dependency : dependencies) {
            Artifact artifact = artifactHelper.resolve(dependency);
            try {
                urls.add(artifact.getFile().toURI().toURL());
            } catch (MalformedURLException e) {
                throw new AssertionError();
            }
        }

        return urls;

    }

}
