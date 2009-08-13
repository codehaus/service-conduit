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
package org.sca4j.tests.function.callback.stateless;

import org.osoa.sca.annotations.OneWay;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * Interface for test cases to verify and reset the callback client.
 *
 * @version $Rev: 2893 $ $Date: 2008-02-26 00:24:57 +0000 (Tue, 26 Feb 2008) $
 */
public interface ClientService {

    @OneWay
    void invoke(CallbackData data);

    String invokeSync(CallbackData data);

    @OneWay
    void invokeServiceReferenceCallback(CallbackData data);

    @OneWay
    public void invokeMultipleHops(CallbackData data);


}
