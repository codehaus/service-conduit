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

package org.sca4j.spi.model.physical;

import java.net.URI;

import org.w3c.dom.Document;

import org.sca4j.scdl.InjectableAttribute;

/**
 * Represents a physical wire source definition.
 *
 * @version $Revision: 5214 $ $Date: 2008-08-18 05:40:58 +0100 (Mon, 18 Aug 2008) $
 */
public class PhysicalWireSourceDefinition {
    private URI uri;
    private URI classLoaderId;
    private InjectableAttribute injectableAttribute;
    private boolean optimizable;
    private Document key;
    private InteractionType type = InteractionType.STATELESS;

    /**
     * Returns the URI of the physical component that is the source of invocations on this wire.
     *
     * @return the URI of the physical component that is the source of invocations on this wire
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the URI of the physical component that is the source of invocations on this wire.
     *
     * @param uri the URI of the physical component that is the source of invocations on this wire
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the id of the classloader associated with the source.
     *
     * @return the id of the classloader associated with the source
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Sets the id of the classloader associated with the source..
     *
     * @param classLoaderId the id of the classloader associated with the source
     */
    public void setClassLoaderId(URI classLoaderId) {
        this.classLoaderId = classLoaderId;
    }


    /**
     * Returns the value source for this wire. This identifies which reference or resource on the component this wire applies to.
     *
     * @return the value source for this wire
     */
    public InjectableAttribute getValueSource() {
        return injectableAttribute;
    }

    /**
     * Sets the value source for this wire.
     *
     * @param injectableAttribute the value source for this wire
     */
    public void setValueSource(InjectableAttribute injectableAttribute) {
        this.injectableAttribute = injectableAttribute;
    }

    /**
     * Returns whether the source side of the wire is optimizable.
     *
     * @return true if the source side of the wire is optimizable
     */
    public boolean isOptimizable() {
        return optimizable;
    }

    /**
     * Sets whether the source side of the wire is optimizable.
     *
     * @param optimizable whether the source side of the wire is optimizable
     */
    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    /**
     * Returns the interaction style for the wire.
     *
     * @return the interaction style for the wire
     */
    public InteractionType getInteractionType() {
        return type;
    }

    /**
     * Sets the interaction style for the wire. If none is set, the default {@link InteractionType#STATELESS} is used.
     *
     * @param type the interaction style for the wire
     */
    public void setInteractionType(InteractionType type) {
        this.type = type;
    }

    /**
     * Returns the key to be used when this wire is part of a Map reference.
     *
     * @return the key to be used when this wire is part of a Map reference
     */
    public Document getKey() {
        return key;
    }

    /**
     * Sets the key to be used when this wire is part of a Map reference.
     *
     * @param key the key to be used when this wire is part of a Map reference
     */
    public void setKey(Document key) {
        this.key = key;
    }
}
