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
package org.sca4j.fabric.services.lcm;

import java.net.URI;
import java.util.Collection;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeReference;
import org.sca4j.scdl.CompositeService;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.lcm.LogicalComponentManagerMBean;
import org.sca4j.spi.services.lcm.LogicalComponentStore;
import org.sca4j.spi.services.lcm.RecoveryException;
import org.sca4j.spi.services.lcm.StoreException;
import org.sca4j.spi.util.UriHelper;

/**
 * @version $Revision$ $Date$
 */
public class LogicalComponentManagerImpl implements LogicalComponentManager, LogicalComponentManagerMBean {

    private LogicalCompositeComponent domain;
    private final LogicalComponentStore logicalComponentStore;

    public LogicalComponentManagerImpl(@Reference LogicalComponentStore logicalComponentStore) {
        this.logicalComponentStore = logicalComponentStore;
    }

    public LogicalComponent<?> getComponent(URI uri) {

        String defragmentedUri = UriHelper.getDefragmentedNameAsString(uri);
        String domainString = domain.getUri().toString();
        String[] hierarchy = defragmentedUri.substring(domainString.length() + 1).split("/");
        String currentUri = domainString;
        LogicalComponent<?> currentComponent = domain;
        for (String name : hierarchy) {
            currentUri = currentUri + "/" + name;
            if (currentComponent instanceof LogicalCompositeComponent) {
                LogicalCompositeComponent composite = (LogicalCompositeComponent) currentComponent;
                currentComponent = composite.getComponent(URI.create(currentUri));
            }
            if (currentComponent == null) {
                return null;
            }
        }
        return currentComponent;

    }

    public Collection<LogicalComponent<?>> getComponents() {
        return domain.getComponents();
    }

    public LogicalCompositeComponent getRootComponent() {
        return domain;
    }

    public void replaceRootComponent(LogicalCompositeComponent component) throws StoreException {
        domain = component;
        logicalComponentStore.store(domain);
    }

    @Init
    public void initialize() throws RecoveryException {
        domain = logicalComponentStore.read();
    }

    public String getDomainURI() {
        return domain.getUri().toString();
    }

    public Composite getDomainComposite() {
        Composite composite = new Composite(new QName(getDomainURI(), "domain"));
        for (LogicalComponent<?> component : domain.getComponents()) {
            composite.add(component.getDefinition());
        }
        for (LogicalService service : domain.getServices()) {
            composite.add((CompositeService) service.getDefinition());
        }
        for (LogicalReference reference : domain.getReferences()) {
            composite.add((CompositeReference) reference.getDefinition());
        }
        return composite;
    }
}
