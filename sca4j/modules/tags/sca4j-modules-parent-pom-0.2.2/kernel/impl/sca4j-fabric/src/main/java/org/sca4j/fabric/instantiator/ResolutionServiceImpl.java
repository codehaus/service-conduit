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
package org.sca4j.fabric.instantiator;

import java.util.List;

import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.instantiator.promotion.PromotionResolutionService;
import org.sca4j.fabric.instantiator.target.TargetResolutionService;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * Default implementation of the resolution service. Resolves promotions first and subsequently invokes a series of resolvers to determine reference
 * targets.
 *
 * @version $Revision$ $Date$
 */
public class ResolutionServiceImpl implements ResolutionService {

    private final PromotionResolutionService promotionResolutionService;
    private final List<TargetResolutionService> targetResolutionServices;

    /**
     * Constructor.
     *
     * @param promotionResolutionService Service for handling promotions.
     * @param targetResolutionServices   An ordered list of target resolution services.
     */
    public ResolutionServiceImpl(@Reference PromotionResolutionService promotionResolutionService,
                                 @Reference List<TargetResolutionService> targetResolutionServices) {
        this.promotionResolutionService = promotionResolutionService;
        this.targetResolutionServices = targetResolutionServices;
    }

    public void resolve(LogicalComponent<?> logicalComponent, LogicalChange change) {
        if (logicalComponent instanceof LogicalCompositeComponent) {
            LogicalCompositeComponent compositeComponent = (LogicalCompositeComponent) logicalComponent;
            for (LogicalComponent<?> child : compositeComponent.getComponents()) {
                resolve(child, change);
            }
        }

        resolveReferences(logicalComponent, change);
        resolveServices(logicalComponent, change);
    }

    public void resolve(LogicalService logicalService, LogicalChange change) {
        promotionResolutionService.resolve(logicalService, change);
    }

    public void resolve(LogicalReference reference, LogicalCompositeComponent component, LogicalChange change) {
        promotionResolutionService.resolve(reference, change);
        for (TargetResolutionService targetResolutionService : targetResolutionServices) {
            targetResolutionService.resolve(reference, component, change);
        }
    }

    /*
     * Handles promotions and target resolution on references.
     */
    private void resolveReferences(LogicalComponent<?> logicalComponent, LogicalChange change) {
        LogicalCompositeComponent parent = logicalComponent.getParent();
        for (LogicalReference logicalReference : logicalComponent.getReferences()) {
            Multiplicity multiplicityValue = logicalReference.getDefinition().getMultiplicity();
            boolean refMultiplicity = multiplicityValue.equals(Multiplicity.ZERO_N) || multiplicityValue.equals(Multiplicity.ONE_N);
            if (refMultiplicity || !logicalReference.isResolved()) {
                // Only resolve references that have not been resolved or ones that are multiplicities since the latter may be reinjected.
                // Explicitly set the reference to unresolved, since if it was a multiplicity it may have been previously resolved.
                logicalReference.setResolved(false);
                promotionResolutionService.resolve(logicalReference, change);
                for (TargetResolutionService targetResolutionService : targetResolutionServices) {
                    targetResolutionService.resolve(logicalReference, parent, change);
                    if (logicalReference.isResolved()) {
                        // the reference has been resolved
                        break;
                    }
                }
                if (!logicalReference.isResolved() && parent.getDefinition().getImplementation().getComponentType().isPromoteUnresolvedReferences()) {
                    parent.addReference(logicalReference);
                }
            }
        }
    }

    /*
     * Handles promotions on services.
     */
    private void resolveServices(LogicalComponent<?> logicalComponent, LogicalChange change) {
        for (LogicalService logicalService : logicalComponent.getServices()) {
            promotionResolutionService.resolve(logicalService, change);
        }
    }

}
