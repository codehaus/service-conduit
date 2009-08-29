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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
