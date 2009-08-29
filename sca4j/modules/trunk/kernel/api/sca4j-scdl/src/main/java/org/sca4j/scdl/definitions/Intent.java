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
