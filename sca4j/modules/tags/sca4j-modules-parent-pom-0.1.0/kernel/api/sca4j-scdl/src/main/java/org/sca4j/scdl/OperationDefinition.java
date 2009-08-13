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
 * Represents an operation definition in the SCDL. 
 * 
 * This is mainly used for declaring operation level intents and policy 
 * sets in the SCDL. The SCA specification currently doesn't support 
 * overloaded operations to be differentiated in the SCDL.
 * 
 * @version $Revision$ $Date$
 */
public class OperationDefinition extends AbstractPolicyAware {
    private static final long serialVersionUID = 3429988354453068399L;

    private String name;

    /**
     * @return Name of the operation as defined in the SCDL.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of the operation as defined in the SCDL.
     */
    public void setName(String name) {
        this.name = name;
    }

}
