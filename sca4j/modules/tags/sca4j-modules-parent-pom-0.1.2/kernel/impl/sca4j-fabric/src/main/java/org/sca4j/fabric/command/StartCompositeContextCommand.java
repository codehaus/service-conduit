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

import java.net.URI;

import org.sca4j.spi.command.AbstractCommand;

/**
 * Starts a composite context on a runtime.
 *
 * @version $Rev: 3680 $ $Date: 2008-04-19 18:47:14 +0100 (Sat, 19 Apr 2008) $
 */
public class StartCompositeContextCommand extends AbstractCommand {
    private final URI groupId;

    public StartCompositeContextCommand(int order, URI groupId) {
        super(order);
        this.groupId = groupId;
    }

    public URI getGroupId() {
        return groupId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StartCompositeContextCommand that = (StartCompositeContextCommand) o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (groupId != null ? groupId.hashCode() : 0);
    }
}
