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
package org.sca4j.scdl.definitions;

import java.util.Set;

import javax.xml.namespace.QName;

import org.osoa.sca.Constants;

/**
 * Represents a registered intent within the domain.
 * 
 * @version $Revision$ $Date$
 *
 */
public final class Intent extends AbstractDefinition {
    private static final long serialVersionUID = 5208711249716729521L;

    /** Binding QName */
    public static final QName BINDING = new QName(Constants.SCA_NS, "binding");
    
    /** Implementation QName */
    public static final QName IMPLEMENTATION = new QName(Constants.SCA_NS, "implementation");
    
    /** Intent type. */
    private IntentType intentType;
    
    /** Name of the qualifiable intent if this is a qualified intent . */
    private QName qualifiable;
    
    /** Whether this intent requires other intents. */
    private Set<QName> requires;
    
    /** Constrained type. */
    private QName constrains;

    /**
     * Initializes the name, description and the constrained artifacts.
     * 
     * @param name Name of the intent.
     * @param description Description of the intent.
     * @param constrains SCA artifact constrained by this intent.
     * @param requires The intents this intent requires if this is a profile intent.
     */
    public Intent(QName name, String description, QName constrains, Set<QName> requires) {
        
        super(name);
        
        if(constrains != null) {
            if(!BINDING.equals(constrains) && !IMPLEMENTATION.equals(constrains)) {
                throw new IllegalArgumentException("Intents can constrain only bindings or implementations");
            }
            intentType = BINDING.equals(constrains) ? IntentType.INTERACTION : IntentType.IMPLEMENTATION;
            this.constrains = constrains;
        }
        
        String localPart = name.getLocalPart();
        if(localPart.indexOf('.') > 0) {
            String qualifiableName = localPart.substring(0, localPart.indexOf('.') + 1);
            qualifiable = new QName(name.getNamespaceURI(), qualifiableName);
        }
        
        this.requires = requires;
        
    }
    
    /**
     * Checks whether this is a profile intent.
     * 
     * @return True if this is a profile intent.
     */
    public boolean isProfile() {
        return requires != null && requires.size() > 0;
    }
    
    /**
     * The intents this intent requires if this is a profile intent.
     * 
     * @return Required intents for a profile intent.
     */
    public Set<QName> getRequires() {
        return requires;
    }
    
    /**
     * Checks whether this is a qualified intent.
     * 
     * @return True if this is a qualified intent.
     */
    public boolean isQualified() {
        return qualifiable != null;
    }
    
    /**
     * Returns the qualifiable intent if this is qualified.
     * 
     * @return Name of the qualifiable intent.
     */
    public QName getQualifiable() {
        return qualifiable;
    }
    
    /**
     * Returns the type of this intent.
     * 
     * @return Type of the intent.
     */
    public IntentType getIntentType() {
        return intentType;
    }
    
    /**
     * Whether this intent constrains the specified type.
     * 
     * @param type Type of the SCA artifact.
     * @return True if this intent constrains the specified type.
     */
    public boolean doesConstrain(QName type) {
        return type.equals(constrains);
    }

}
