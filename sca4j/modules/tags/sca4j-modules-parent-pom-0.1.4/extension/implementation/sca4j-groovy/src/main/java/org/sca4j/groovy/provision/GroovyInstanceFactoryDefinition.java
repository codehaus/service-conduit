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
package org.sca4j.groovy.provision;

import org.sca4j.pojo.provision.InstanceFactoryDefinition;

/**
 * @version $Rev: 5245 $ $Date: 2008-08-20 22:25:50 +0100 (Wed, 20 Aug 2008) $
 */
public class GroovyInstanceFactoryDefinition extends InstanceFactoryDefinition {
    private String scriptName;

    /**
     * Returns the name of a file resource containing the implementation as Groovy script.
     *
     * @return the name of the script file
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Sets the name of a file resource containing the implementation as Groovy script.
     *
     * @param scriptName the name of the script file
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}
