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
                if (service.getDefinition().isManagement()) {
                    continue;
                }
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
