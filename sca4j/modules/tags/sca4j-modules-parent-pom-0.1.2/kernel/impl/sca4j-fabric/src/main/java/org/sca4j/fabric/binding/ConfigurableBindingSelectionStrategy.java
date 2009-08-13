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

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Property;

import org.sca4j.spi.binding.BindingProvider;
import org.sca4j.spi.binding.BindingSelectionStrategy;

/**
 * A BindingSelectionStrategy that makes a selection based on an ordered list of bindings. This list is provided via a property which can be sourced
 * from a runtime domain level property specified in systemConfig.xml.
 *
 * @version $Revision$ $Date$
 */
public class ConfigurableBindingSelectionStrategy implements BindingSelectionStrategy {
    private List<QName> scaBindingOrder;

    @Property
    public void setScaBindingOrder(List<QName> scaBindingOrder) {
        this.scaBindingOrder = scaBindingOrder;
    }

    public BindingProvider select(Map<QName, BindingProvider> providers) {
        if (scaBindingOrder == null) {
            return providers.values().iterator().next();
        }
        BindingProvider provider = null;
        for (QName name : scaBindingOrder) {
            provider = providers.get(name);
            if (provider != null) {
                break;
            }
        }
        if (provider == null) {
            // none of the ordered bindings were found, default to the first one
            // TODO support exclusions
            return providers.values().iterator().next();
        }
        return provider;
    }
}
