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
package org.sca4j.binding.hessian.control;

import java.net.URI;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.hessian.scdl.HessianBindingDefinition;
import org.sca4j.host.Namespaces;
import org.sca4j.spi.binding.BindingProvider;
import org.sca4j.spi.binding.BindingSelectionException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.topology.RuntimeInfo;
import org.sca4j.spi.services.discovery.DiscoveryService;

/**
 * Allows Hessian to be used for sca.binding in a domain.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class HessianBindingProvider implements BindingProvider {
    private static final QName HTTP = new QName(Namespaces.SCA4J_NS, "transport.http.base");
    private static final QName BINDING_QNAME = new QName(Namespaces.SCA4J_NS, "binding.hessian");

    private DiscoveryService discoveryService;

    public HessianBindingProvider(@Reference DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public MatchType canBind(LogicalReference source, LogicalService target) {
        // TODO handle must provide intents
        return MatchType.REQUIRED_INTENTS;
    }

    public void bind(LogicalReference source, LogicalService target) throws BindingSelectionException {
        URI runtimeId = target.getParent().getRuntimeId();
        RuntimeInfo targetInfo = discoveryService.getRuntimeInfo(runtimeId);
        if (targetInfo == null) {
            // This could potentially occur if a runtime is removed from the domain during deployment
            throw new BindingSelectionException("Runtime not found: " + runtimeId);
        }
        // determing whether to configure both sides of the wire or just the reference
        if (target.getBindings().isEmpty()) {
            // configure both sides
            configureService(target);
            configureReference(source, target, targetInfo);
        } else {
            configureReference(source, target, targetInfo);
        }
    }

    private void configureReference(LogicalReference source, LogicalService target, RuntimeInfo targetInfo) throws BindingSelectionException {
        LogicalBinding<HessianBindingDefinition> binding = null;
        for (LogicalBinding<?> entry : target.getBindings()) {
            if (entry.getBinding().getType().equals(BINDING_QNAME)) {
                //noinspection unchecked
                binding = (LogicalBinding<HessianBindingDefinition>) entry;
                break;
            }
        }
        if (binding == null) {
            throw new BindingSelectionException("Hessian binding on service not found: " + target.getUri());
        }
        URI targetUri = URI.create("http://" + targetInfo.getTransportMetaData(HTTP) + binding.getBinding().getTargetUri().toString());
        constructLogicalReference(source, targetUri);
    }

    private void constructLogicalReference(LogicalReference source, URI targetUri) {
        HessianBindingDefinition referenceDefinition = new HessianBindingDefinition(targetUri, null);
        LogicalBinding<HessianBindingDefinition> referenceBinding = new LogicalBinding<HessianBindingDefinition>(referenceDefinition, source);
        source.addBinding(referenceBinding);
    }

    private void configureService(LogicalService target) {
        String endpointName = target.getUri().getPath() + "/" + target.getUri().getFragment();
        URI endpointUri = URI.create(endpointName);
        HessianBindingDefinition serviceDefinition = new HessianBindingDefinition(endpointUri, null);
        LogicalBinding<HessianBindingDefinition> serviceBinding = new LogicalBinding<HessianBindingDefinition>(serviceDefinition, target);
        target.addBinding(serviceBinding);
    }
}
