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
package org.sca4j.introspection;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * Default implementation of an IntrospectionContext.
 *
 * @version $Rev: 4336 $ $Date: 2008-05-25 10:06:15 +0100 (Sun, 25 May 2008) $
 */
public class DefaultIntrospectionContext implements IntrospectionContext {
    private final List<ValidationFailure> errors = new ArrayList<ValidationFailure>();
    private final List<ValidationFailure> warnings = new ArrayList<ValidationFailure>();
    private final ClassLoader targetClassLoader;
    private final URL sourceBase;
    private final String targetNamespace;
    private final URI contributionUri;
    private final TypeMapping typeMapping;

    public DefaultIntrospectionContext(ClassLoader targetClassLoader,
                                       URL sourceBase,
                                       String targetNamespace,
                                       URI contributionUri,
                                       TypeMapping typeMapping) {
        this.targetClassLoader = targetClassLoader;
        this.sourceBase = sourceBase;
        this.targetNamespace = targetNamespace;
        this.contributionUri = contributionUri;
        this.typeMapping = typeMapping;
    }

    public DefaultIntrospectionContext(URI contributionUri, ClassLoader classLoader, String targetNamespace) {
        this(classLoader, null, targetNamespace, contributionUri, null);
    }

    /**
     * Constructor defining properties of this context.
     *
     * @param classLoader     the classloader for loading application resources
     * @param contributionUri the active contribution URI
     * @param scdlLocation    the location of the SCDL defining this composite
     */
    public DefaultIntrospectionContext(ClassLoader classLoader, URI contributionUri, URL scdlLocation) {
        this(classLoader, scdlLocation, null, contributionUri, null);
    }

    /**
     * Initializes from a parent context.
     *
     * @param parentContext   Parent context.
     * @param targetNamespace Target namespace.
     */
    public DefaultIntrospectionContext(IntrospectionContext parentContext, String targetNamespace) {
        this(parentContext.getTargetClassLoader(),
             parentContext.getSourceBase(),
             targetNamespace,
             parentContext.getContributionUri(),
             parentContext.getTypeMapping());
    }

    /**
     * Initializes from a parent context.
     *
     * @param parentContext Parent context.
     * @param typeMapping   mapping of formal types
     */
    public DefaultIntrospectionContext(IntrospectionContext parentContext, TypeMapping typeMapping) {
        this(parentContext.getTargetClassLoader(),
             parentContext.getSourceBase(),
             parentContext.getTargetNamespace(),
             parentContext.getContributionUri(),
             typeMapping);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<ValidationFailure> getErrors() {
        return errors;
    }

    public void addError(ValidationFailure e) {
        errors.add(e);
    }

    public void addErrors(List<ValidationFailure> errors) {
        this.errors.addAll(errors);
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public List<ValidationFailure> getWarnings() {
        return warnings;
    }

    public void addWarning(ValidationFailure e) {
        warnings.add(e);
    }

    public void addWarnings(List<ValidationFailure> warnings) {
        this.warnings.addAll(warnings);
    }

    public ClassLoader getTargetClassLoader() {
        return targetClassLoader;
    }

    public URL getSourceBase() {
        return sourceBase;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public URI getContributionUri() {
        return contributionUri;
    }

    public TypeMapping getTypeMapping() {
        return typeMapping;
    }
}
