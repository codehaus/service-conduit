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
package org.sca4j.scdl;

import java.lang.reflect.Constructor;

/**
 * @version $Rev: 5233 $ $Date: 2008-08-19 22:50:45 +0100 (Tue, 19 Aug 2008) $
 */
public class ConstructorInjectionSite extends InjectionSite {
    private static final long serialVersionUID = -6543986170145816234L;
    private Signature signature;
    private int param;

    public ConstructorInjectionSite(Constructor<?> constructor, int param) {
        super(constructor.getParameterTypes()[param].getName());
        this.signature = new Signature(constructor);
        this.param = param;
    }

    public ConstructorInjectionSite(Signature signature, int param) {
        super(signature.getParameterTypes().get(param));
        this.signature = signature;
        this.param = param;
    }

    /**
     * Returns the signature that identifies the method.
     *
     * @return the signature that identifies the method
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Returns the index of the parameter being injected.
     *
     * @return the index of the parameter being injected
     */
    public int getParam() {
        return param;
    }

    public String toString() {
        return signature.toString() + '[' + param + ']';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstructorInjectionSite that = (ConstructorInjectionSite) o;

        return param == that.param && signature.equals(that.signature);

    }

    public int hashCode() {
        return 31 * signature.hashCode() + param;
    }
}
