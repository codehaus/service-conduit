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

import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * @version $Rev: 2893 $ $Date: 2008-02-26 00:24:57 +0000 (Tue, 26 Feb 2008) $
 */
@Service(interfaces = {ForwardService.class, CallbackService.class})
public class ForwardServiceImpl implements ForwardService, CallbackService {
    @Reference
    protected EndService endService;

    @Callback
    protected CallbackService callbackService;

    @Callback
    protected ServiceReference<CallbackService> reference;

    public void invoke(CallbackData data) {
        callbackService.onCallback(data);
    }

    public String invokeSync(CallbackData data) {
        callbackService.onSyncCallback(data);
        return "receipt";
    }

    public void invokeServiceReferenceCallback(CallbackData data) {
        reference.getService().onServiceReferenceCallback(data);
    }

    public void invokeMultipleHops(CallbackData data) {
        endService.invoke(data);
    }

    public void onCallback(CallbackData data) {
        callbackService.onCallback(data);
    }

    public void onServiceReferenceCallback(CallbackData data) {
        callbackService.onServiceReferenceCallback(data);
    }

    public void onSyncCallback(CallbackData data) {
        callbackService.onSyncCallback(data);
    }
}
