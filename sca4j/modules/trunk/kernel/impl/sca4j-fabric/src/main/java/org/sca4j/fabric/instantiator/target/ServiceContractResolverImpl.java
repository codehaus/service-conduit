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

package org.sca4j.fabric.instantiator.target;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.util.UriHelper;

/**
 * @version $Revision$ $Date$
 */
public class ServiceContractResolverImpl implements ServiceContractResolver {

    public ServiceContract determineContract(LogicalService service) {
        ServiceContract contract = service.getDefinition().getServiceContract();
        if (contract != null) {
            return contract;
        }
        if (!(service.getParent() instanceof LogicalCompositeComponent)) {
            return null;
        }
        LogicalCompositeComponent parent = (LogicalCompositeComponent) service.getParent();
        URI promotedUri = service.getPromotedUri();
        URI name = UriHelper.getDefragmentedName(promotedUri);
        LogicalComponent<?> promoted = parent.getComponent(UriHelper.getDefragmentedName(promotedUri));
        if (promoted == null) {
            // This error should be caught by validation before this point
            throw new AssertionError("Promoted component " + name + " not found in " + parent.getUri());
        }
        String serviceName = promotedUri.getFragment();
        LogicalService promotedService;
        if (serviceName == null && promoted.getServices().size() == 1) {
            // select the default service as a service name was not specified
            Collection<LogicalService> services = promoted.getServices();
            promotedService = services.iterator().next();
        } else if (serviceName == null) {
            // programing error
            throw new AssertionError("Service must be specified for promotion: " + promotedUri);
        } else {
            promotedService = promoted.getService(serviceName);
        }
        if (promotedService == null) {
            throw new AssertionError("Promoted service not found: " + promotedUri);
        }
        return determineContract(promotedService);
    }

    public ServiceContract determineContract(LogicalReference reference) {
        ServiceContract contract = reference.getDefinition().getServiceContract();
        if (contract != null) {
            return contract;
        }
        if (!(reference.getParent() instanceof LogicalCompositeComponent)) {
            return null;
        }
        LogicalCompositeComponent parent = (LogicalCompositeComponent) reference.getParent();
        //URI promotedUri = service.getPromotedUri();
        List<URI> promotedUris = reference.getPromotedUris();
        if (promotedUris.size() < 1) {
            // this is an invalid configuration: a reference with no service contract that does not promote another reference and should be
            // caught during the load phase before reaching here.
            throw new AssertionError();
        }
        // pick the first one since references expose the same contract
        URI promotedUri = promotedUris.get(0);

        LogicalComponent<?> promoted = parent.getComponent(UriHelper.getDefragmentedName(promotedUri));
        if (promoted == null) {
            throw new AssertionError("Promoted reference not found : " + promotedUri);
        }
        String referenceName = promotedUri.getFragment();
        LogicalReference promotedReference;
        if (referenceName == null && promoted.getReferences().size() == 1) {
            // select the default reference as a reference name was not specified
            Collection<LogicalReference> references = promoted.getReferences();
            promotedReference = references.iterator().next();
        } else if (referenceName == null) {
            // programing error
            throw new AssertionError("Reference name must be specified on " + promoted.getDefinition().getName());
        } else {
            promotedReference = promoted.getReference(referenceName);
        }
        if (promotedReference == null) {
            throw new AssertionError("Promoted reference " + referenceName + " not found on " + promoted.getDefinition().getName());
        }
        return determineContract(promotedReference);
    }

}
