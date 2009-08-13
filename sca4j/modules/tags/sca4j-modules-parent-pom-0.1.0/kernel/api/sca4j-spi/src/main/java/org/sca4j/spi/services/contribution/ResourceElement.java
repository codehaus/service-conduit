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
package org.sca4j.spi.services.contribution;

import java.io.Serializable;

/**
 * An addressable part of a Resource, such as a WSDL PortType, ComponentType, or Schema.
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class ResourceElement<SYMBOL extends Symbol, VALUE extends Serializable> implements Serializable {
    private static final long serialVersionUID = 7148942706569626009L;
    private SYMBOL symbol;
    private VALUE value;

    public ResourceElement(SYMBOL symbol) {
        this.symbol = symbol;
    }

    public ResourceElement(SYMBOL symbol, VALUE value) {
        this.symbol = symbol;
        this.value = value;
    }

    public SYMBOL getSymbol() {
        return symbol;
    }

    public VALUE getValue() {
        return value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }
}
