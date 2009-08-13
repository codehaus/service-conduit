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
