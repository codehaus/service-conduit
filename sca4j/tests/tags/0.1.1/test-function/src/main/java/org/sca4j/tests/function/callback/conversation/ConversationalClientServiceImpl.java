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

package org.sca4j.tests.function.callback.conversation;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * @version $Revision$ $Date$
 */
@Service(interfaces = {ConversationalClientService.class, CallbackService.class})
@Scope("CONVERSATION")
public class ConversationalClientServiceImpl implements ConversationalClientService, CallbackService {
    private int count;
    private CallbackData data;

    @Reference
    protected ForwardService forwardService;

    public void invoke(CallbackData data) {
        count++;
        this.data = data;
        forwardService.invoke();
    }

    public int getCount() {
        return count;
    }

    public void onCallback() {
        count++;
        if (forwardService.getCount() != 1) {
            //noinspection ThrowableInstanceNeverThrown
            AssertionError e = new AssertionError("Forward servsice count incorrect");
            data.setException(e);
        }
        forwardService.invoke2();
    }

    public void end() {
        if (!data.isError() && forwardService.getCount() != 2) {
            //noinspection ThrowableInstanceNeverThrown
            AssertionError e = new AssertionError("Forward service count incorrect");
            data.setException(e);
        } else if (!data.isError()) {
            count++;
            data.callback();
        }
        data.getLatch().countDown();
    }
}
