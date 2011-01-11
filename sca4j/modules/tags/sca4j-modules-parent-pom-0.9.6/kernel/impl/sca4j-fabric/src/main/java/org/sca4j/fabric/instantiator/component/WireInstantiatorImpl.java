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
package org.sca4j.fabric.instantiator.component;

import java.net.URI;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.WireDefinition;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.instance.LogicalWire;
import org.sca4j.spi.util.UriHelper;

/**
 * Default implementation of the WireInstantiator.
 *
 * @version $Revision$ $Date$
 */
public class WireInstantiatorImpl implements WireInstantiator {

    public void instantiateWires(Composite composite, LogicalCompositeComponent parent, LogicalChange change) {
        String baseUri = parent.getUri().toString();
        // instantiate wires held directly in the composite and in included composites
        for (WireDefinition definition : composite.getWires()) {
            // resolve the source reference
            // source URI is relative to the parent composite the include is targeted to
            URI sourceUri = URI.create(baseUri + "/" + UriHelper.getDefragmentedName(definition.getSource()));
            String referenceName = definition.getSource().getFragment();
            LogicalReference logicalReference = resolveLogicalReference(referenceName, sourceUri, parent, change);
            if (logicalReference == null) {
                // error resolving, continue
                continue;
            }

            // resolve the target service
            URI targetUri = URI.create(baseUri + "/" + definition.getTarget());
            targetUri = resolveTargetUri(targetUri, parent, change);
            if (targetUri == null) {
                // error resolving
                continue;
            }

            // create the wire
            LogicalWire wire = new LogicalWire(parent, logicalReference, targetUri);
            parent.addWire(logicalReference, wire);
            change.addWire(wire);
        }
    }

    private LogicalReference resolveLogicalReference(String referenceName, URI sourceUri, LogicalCompositeComponent parent, LogicalChange change) {
        LogicalComponent<?> source = parent.getComponent(sourceUri);
        if (source == null) {
            WireSourceNotFound error = new WireSourceNotFound(sourceUri, parent.getUri());
            change.addError(error);
            return null;
        }
        LogicalReference logicalReference;
        if (referenceName == null) {
            // a reference was not specified
            if (source.getReferences().size() == 0) {
                WireSourceNoReference error = new WireSourceNoReference(sourceUri, parent.getUri());
                change.addError(error);
                return null;
            } else if (source.getReferences().size() != 1) {
                WireSourceAmbiguousReference error = new WireSourceAmbiguousReference(sourceUri, parent.getUri());
                change.addError(error);
                return null;
            }
            // default to the only reference
            logicalReference = source.getReferences().iterator().next();
        } else {
            logicalReference = source.getReference(referenceName);
            if (logicalReference == null) {
                WireSourceReferenceNotFound error = new WireSourceReferenceNotFound(sourceUri, referenceName, parent.getUri());
                change.addError(error);
                return null;
            }
        }
        return logicalReference;
    }

    /**
     * Resolves the wire target URI to a service provided by a component in the parent composite.
     *
     * @param targetUri the atrget URI to resolve.
     * @param parent    the parent composite to resolve against
     * @param change    the logical change to report errors against
     * @return the fully resolved wire target URI
     */
    private URI resolveTargetUri(URI targetUri, LogicalCompositeComponent parent, LogicalChange change) {
        URI targetComponentUri = UriHelper.getDefragmentedName(targetUri);
        LogicalComponent<?> targetComponent = parent.getComponent(targetComponentUri);
        if (targetComponent == null) {
            WireTargetNotFound error = new WireTargetNotFound(targetUri, parent.getUri());
            change.addError(error);
            return null;
        }

        String serviceName = targetUri.getFragment();
        if (serviceName != null) {
            if (targetComponent.getService(serviceName) == null) {
                WireTargetServiceNotFound error = new WireTargetServiceNotFound(targetUri, parent.getUri());
                change.addError(error);
                return null;
            }
            return targetUri;
        } else {
            LogicalService target = null;
            for (LogicalService service : targetComponent.getServices()) {
                if (target != null) {
                    AmbiguousWireTargetService error = new AmbiguousWireTargetService(targetUri, parent.getUri());
                    change.addError(error);
                    return null;
                }
                target = service;
            }
            if (target == null) {
                WireTargetNoService error = new WireTargetNoService(targetUri, parent.getUri());
                change.addError(error);
                return null;
            }
            return target.getUri();
        }

    }

}
