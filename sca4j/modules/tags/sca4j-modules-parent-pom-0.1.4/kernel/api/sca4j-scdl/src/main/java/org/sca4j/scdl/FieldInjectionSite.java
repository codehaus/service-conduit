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

import java.lang.reflect.Field;

/**
 * @version $Rev: 5233 $ $Date: 2008-08-19 22:50:45 +0100 (Tue, 19 Aug 2008) $
 */
public class FieldInjectionSite extends InjectionSite {
    private static final long serialVersionUID = -6502983302874808563L;
    private String name;
    int modifiers;

    public FieldInjectionSite(Field field) {
        super(field.getType().getName());
        this.modifiers = field.getModifiers();
        name = field.getName();
    }

    /**
     * Gets the name of the field.
     *
     * @return Site name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the field modifiers.
     *
     * @return the field modifiers
     */
    public int getModifiers() {
        return modifiers;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldInjectionSite that = (FieldInjectionSite) o;
        return name.equals(that.name);

    }

    public int hashCode() {
        return name.hashCode();
    }
}
