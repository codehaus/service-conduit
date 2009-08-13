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
package org.sca4j.tests.binding.harness;

import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 2428 $ $Date: 2008-01-04 20:08:39 +0000 (Fri, 04 Jan 2008) $
 */
public class EchoDelegator implements EchoService {
    private final EchoService delegate;

    public EchoDelegator(@Reference EchoService delegate) {
        this.delegate = delegate;
    }

    public String echoString(String message) {
        return delegate.echoString(message);
    }

    public int echoInt(int value) {
        return delegate.echoInt(value);
    }

    public void echoFault() throws EchoFault {
        delegate.echoFault();
    }
}
