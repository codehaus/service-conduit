/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.tests.function.callback.stateless;

import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * @version $Rev: 2893 $ $Date: 2008-02-26 00:24:57 +0000 (Tue, 26 Feb 2008) $
 */
@Service(value = {ForwardService.class, CallbackService.class})
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
