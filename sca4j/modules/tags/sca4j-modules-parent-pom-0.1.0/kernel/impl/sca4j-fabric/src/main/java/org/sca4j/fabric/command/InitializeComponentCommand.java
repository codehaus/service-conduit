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
package org.sca4j.fabric.command;

import org.sca4j.spi.command.AbstractCommand;

/**
 * A command to initialize a composite scoped component on a runtime.
 *
 * @version $Rev: 3680 $ $Date: 2008-04-19 18:47:14 +0100 (Sat, 19 Apr 2008) $
 */
public class InitializeComponentCommand extends AbstractCommand {
    private final ComponentInitializationUri uri;


    public InitializeComponentCommand(int order, ComponentInitializationUri uri) {
        super(order);
        this.uri = uri;
    }

    public ComponentInitializationUri getUri() {
        return uri;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InitializeComponentCommand that = (InitializeComponentCommand) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (uri != null ? uri.hashCode() : 0);
    }
}
