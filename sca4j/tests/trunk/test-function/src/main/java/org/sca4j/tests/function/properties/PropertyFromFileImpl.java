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
package org.sca4j.tests.function.properties;

import org.osoa.sca.annotations.Property;

/**
 * @version $Rev: 1517 $ $Date: 2007-10-13 11:21:17 +0100 (Sat, 13 Oct 2007) $
 */
public class PropertyFromFileImpl implements PropertyFromFile {
    private String string;

    @Property
    public void setString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
