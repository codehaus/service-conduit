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

/**
 * Identifies an attribute of the component that can be injected into an instance.
 *
 * @version $Revision: 5236 $ $Date: 2008-08-20 19:46:18 +0100 (Wed, 20 Aug 2008) $
 */
public class InjectableAttribute extends ModelObject {
    private static final long serialVersionUID = -3313258224983902890L;
    public static final InjectableAttribute COMPONENT_CONTEXT = new InjectableAttribute(InjectableAttributeType.CONTEXT, "ComponentContext");
    public static final InjectableAttribute REQUEST_CONTEXT = new InjectableAttribute(InjectableAttributeType.CONTEXT, "RequestContext");
    public static final InjectableAttribute CONVERSATION_ID = new InjectableAttribute(InjectableAttributeType.CONTEXT, "ConversationId");

    private InjectableAttributeType valueType;

    private String name;

    /**
     * Constructor used for deserialization.
     */
    public InjectableAttribute() {
    }

    /**
     * Constructor specifying type of value and logical name.
     *
     * @param valueType the type of value
     * @param name      the logical name
     */
    public InjectableAttribute(InjectableAttributeType valueType, String name) {
        this.valueType = valueType;
        this.name = name;
    }

    /**
     * Returns the type (service, reference, property).
     *
     * @return the type of value this source represents
     */
    public InjectableAttributeType getValueType() {
        return valueType;
    }

    /**
     * Sets the type (callback, reference, property).
     *
     * @param valueType the type of value this source represents
     */
    public void setValueType(InjectableAttributeType valueType) {
        this.valueType = valueType;
    }

    /**
     * Returns the name.
     *
     * @return the name of this value
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this value.
     *
     * @param name the name of this value
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + '[' + valueType + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InjectableAttribute that = (InjectableAttribute) o;
        return name.equals(that.name) && valueType == that.valueType;

    }

    @Override
    public int hashCode() {
        return valueType.hashCode() * 31 + name.hashCode();
    }
}
