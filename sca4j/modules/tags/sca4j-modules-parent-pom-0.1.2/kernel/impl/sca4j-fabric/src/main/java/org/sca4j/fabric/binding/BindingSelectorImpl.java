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
package org.sca4j.fabric.binding;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.binding.BindingProvider;
import org.sca4j.spi.binding.BindingSelectionException;
import org.sca4j.spi.binding.BindingSelectionStrategy;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.instance.LogicalWire;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.util.UriHelper;

/**
 * Selects a binding provider by delegating to a BindingSelectionStrategy configured for the domain. For each wire, if a remote service has an
 * explicit binding, its configuration will be used to construct the reference binding. If a service does not have an explicit binding, the wire is
 * said to using binding.sca, in which case the BindingSelector will select an appropriate remote transport and create binding configuraton for both
 * sides of the wire.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class BindingSelectorImpl implements BindingSelector {
    private Map<QName, BindingProvider> providers = new HashMap<QName, BindingProvider>();
    private BindingSelectionStrategy strategy;
    private LogicalComponentManager logicalComponentManager;

    public BindingSelectorImpl(@Reference(name = "logicalComponentManager")LogicalComponentManager logicalComponentManager) {
        this.logicalComponentManager = logicalComponentManager;
    }

    /**
     * Lazily injects SCAServiceProviders as they become available from runtime extensions.
     *
     * @param providers the set of providers
     */
    @Reference(required = false)
    public void setProviders(Map<QName, BindingProvider> providers) {
        this.providers = providers;
    }

    @Reference(required = false)
    public void setStrategy(BindingSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void selectBindings(LogicalComponent<?> component) throws BindingSelectionException {
        for (LogicalReference reference : component.getReferences()) {
            for (LogicalWire wire : reference.getWires()) {
                if (wire.getTargetUri() != null) {
                    URI targetUri = UriHelper.getDefragmentedName(wire.getTargetUri());
                    LogicalComponent target = logicalComponentManager.getComponent(targetUri);
                    assert target != null;
                    if ((component.getRuntimeId() == null && target.getRuntimeId() == null)) {
                        // components are local, no need for a binding
                        continue;
                    } else if (component.getRuntimeId() != null && component.getRuntimeId().equals(target.getRuntimeId())) {
                        // components are local, no need for a binding
                        continue;
                    }
                    LogicalService targetServce = target.getService(wire.getTargetUri().getFragment());
                    assert targetServce != null;
                    selectBinding(reference, targetServce);
                }
            }
        }

    }

    /**
     * Selects and configures a binding to connect the source to the target.
     *
     * @param source the source reference
     * @param target the target reference
     * @throws BindingSelectionException if an error occurs selecting a binding
     */
    private void selectBinding(LogicalReference source, LogicalService target) throws BindingSelectionException {
        Map<QName, BindingProvider> requiredMatches = new HashMap<QName, BindingProvider>();
        Map<QName, BindingProvider> allMatches = new HashMap<QName, BindingProvider>();

        for (Map.Entry<QName, BindingProvider> entry : providers.entrySet()) {
            BindingProvider.MatchType matchType = entry.getValue().canBind(source, target);
            switch (matchType) {
            case ALL_INTENTS:
                allMatches.put(entry.getKey(), entry.getValue());
                break;
            case REQUIRED_INTENTS:
                requiredMatches.put(entry.getKey(), entry.getValue());
                break;
            case NO_MATCH:
                break;
            }
        }
        if (!allMatches.isEmpty()) {
            strategy.select(allMatches).bind(source, target);
        } else if (!requiredMatches.isEmpty()) {
            strategy.select(requiredMatches).bind(source, target);
        } else {
            URI sourceUri = source.getUri();
            URI targetUri = target.getUri();
            throw new NoSCABindingProviderException("No SCA binding provider suitable for creating wire from " + sourceUri + " to " + targetUri);
        }
    }

}

