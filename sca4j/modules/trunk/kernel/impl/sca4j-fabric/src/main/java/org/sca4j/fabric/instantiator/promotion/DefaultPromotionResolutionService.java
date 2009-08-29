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
package org.sca4j.fabric.instantiator.promotion;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.sca4j.fabric.instantiator.AmbiguousReference;
import org.sca4j.fabric.instantiator.AmbiguousService;
import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.instantiator.NoServiceOnComponent;
import org.sca4j.fabric.instantiator.PromotedComponentNotFound;
import org.sca4j.fabric.instantiator.ReferenceNotFound;
import org.sca4j.fabric.instantiator.ServiceNotFound;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.util.UriHelper;

/**
 * Default implementation of the promotion service.
 *
 * @version $Revision$ $Date$
 */
public class DefaultPromotionResolutionService implements PromotionResolutionService {

    public void resolve(LogicalService logicalService, LogicalChange change) {

        URI promotedUri = logicalService.getPromotedUri();

        if (promotedUri == null) {
            return;
        }

        URI promotedComponentUri = UriHelper.getDefragmentedName(promotedUri);
        String promotedServiceName = promotedUri.getFragment();

        LogicalCompositeComponent composite = (LogicalCompositeComponent) logicalService.getParent();
        LogicalComponent<?> promotedComponent = composite.getComponent(promotedComponentUri);

        if (promotedComponent == null) {
            PromotedComponentNotFound error = new PromotedComponentNotFound(logicalService, promotedComponentUri);
            change.addError(error);
            return;
        }

        if (promotedServiceName == null) {
            Collection<LogicalService> componentServices = promotedComponent.getServices();
            if (componentServices.size() == 0) {
                String msg = "No services available on component: " + promotedComponentUri;
                NoServiceOnComponent error = new NoServiceOnComponent(msg, logicalService);
                change.addError(error);
                return;
            } else if (componentServices.size() != 1) {
                String msg = "The promoted service " + logicalService.getUri() + " must explicitly specify the service it is promoting on component "
                        + promotedComponentUri + " as the component has more than one service";
                AmbiguousService error = new AmbiguousService(logicalService, msg);
                change.addError(error);
                return;
            }
            logicalService.setPromotedUri(componentServices.iterator().next().getUri());
        } else {
            if (promotedComponent.getService(promotedServiceName) == null) {
                String message =
                        "Service " + promotedServiceName + " promoted from " + logicalService.getUri() + " not found on component " + promotedComponentUri;
                ServiceNotFound error = new ServiceNotFound(message, logicalService, promotedComponentUri);
                change.addError(error);
            }
        }

    }

    public void resolve(LogicalReference logicalReference, LogicalChange change) {

        List<URI> promotedUris = logicalReference.getPromotedUris();

        for (int i = 0; i < promotedUris.size(); i++) {

            URI promotedUri = promotedUris.get(i);

            URI promotedComponentUri = UriHelper.getDefragmentedName(promotedUri);
            String promotedReferenceName = promotedUri.getFragment();

            LogicalCompositeComponent parent = (LogicalCompositeComponent) logicalReference.getParent();
            LogicalComponent<?> promotedComponent = parent.getComponent(promotedComponentUri);

            if (promotedComponent == null) {
                PromotedComponentNotFound error = new PromotedComponentNotFound(logicalReference, promotedComponentUri);
                change.addError(error);
                return;
            }

            if (promotedReferenceName == null) {
                Collection<LogicalReference> componentReferences = promotedComponent.getReferences();
                if (componentReferences.size() == 0) {
                    String msg = "Reference " + promotedReferenceName + " not found on component " + promotedComponentUri;
                    ReferenceNotFound error = new ReferenceNotFound(msg, promotedComponent, promotedReferenceName);
                    change.addError(error);
                    return;
                } else if (componentReferences.size() > 1) {
                    AmbiguousReference error = new AmbiguousReference(logicalReference, promotedComponentUri);
                    change.addError(error);
                    return;
                }
                logicalReference.setPromotedUri(i, componentReferences.iterator().next().getUri());
            } else if (promotedComponent.getReference(promotedReferenceName) == null) {
                String msg = "Reference " + promotedReferenceName + " not found on component " + promotedComponentUri;
                ReferenceNotFound error = new ReferenceNotFound(msg, promotedComponent, promotedReferenceName);
                change.addError(error);
                return;
            }

        }

    }

}
