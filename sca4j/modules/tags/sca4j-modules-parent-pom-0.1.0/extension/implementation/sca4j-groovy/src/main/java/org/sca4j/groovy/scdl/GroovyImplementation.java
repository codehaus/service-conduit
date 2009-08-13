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
package org.sca4j.groovy.scdl;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Implementation;

/**
 * A component implemented in Groovy. The implementation can be a script in source or compiled form.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class GroovyImplementation extends Implementation<PojoComponentType> {
    private static final long serialVersionUID = -8092204063300139457L;
    public static final QName IMPLEMENTATION_GROOVY = new QName(Namespaces.SCA4J_NS, "groovy");

    private String scriptName;
    private String className;

    public GroovyImplementation() {
    }

    public GroovyImplementation(String scriptName, String className) {
        this.scriptName = scriptName;
        this.className = className;
    }

    public GroovyImplementation(String scriptName, String className, PojoComponentType componentType) {
        super(componentType);
        this.scriptName = scriptName;
        this.className = className;
    }

    public QName getType() {
        return IMPLEMENTATION_GROOVY;
    }

    /**
     * Returns the name of a file containing the script source.
     *
     * @return the name of a file containing the script source
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Sets the name of a file containing the script source.
     *
     * @param scriptName the name of a file containing the script source
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * Returns the name of a compiled Groovy class.
     *
     * @return the name of a compiled Groovy class
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of a compiled Groovy class.
     *
     * @param className the name of a compiled Groovy class
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
