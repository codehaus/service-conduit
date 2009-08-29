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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Super class for logical services and references.
 *
 * @version $Rev: 59 $ $Date: 2007-05-19 08:21:09 +0100 (Sat, 19 May 2007) $
 */
public abstract class Bindable extends LogicalScaArtifact<LogicalComponent<?>> {
    private static final long serialVersionUID = 570403036597601956L;
    private final List<LogicalBinding<?>> bindings;
    private final List<LogicalBinding<?>> callbackBindings;

    /**
     * Initializes the URI and parent for the service or the reference.
     *
     * @param uri    URI of the service or the reference.
     * @param parent Parent of the service or the reference.
     * @param type   Type of this artifact (service or reference).
     */
    protected Bindable(URI uri, LogicalComponent<?> parent, QName type) {
        super(uri, parent, type);
        bindings = new ArrayList<LogicalBinding<?>>();
        callbackBindings = new ArrayList<LogicalBinding<?>>();
    }

    /**
     * Overrides all the current bindings for the service or reference.
     *
     * @param bindings New set of bindings.
     */
    public final void overrideBindings(List<LogicalBinding<?>> bindings) {
        this.bindings.clear();
        this.bindings.addAll(bindings);
    }

    /**
     * Overrides all the current callback bindings for the service or reference.
     *
     * @param bindings New set of bindings.
     */
    public final void overrideCallbackBindings(List<LogicalBinding<?>> bindings) {
        this.callbackBindings.clear();
        this.callbackBindings.addAll(bindings);
    }

    /**
     * Returns all the bindings on the service or the reference.
     *
     * @return The bindings available on the service or the reference.
     */
    public final List<LogicalBinding<?>> getBindings() {
        return bindings;
    }

    /**
     * Returns all the callback bindings on the service or the reference.
     *
     * @return The bindings available on the service or the reference.
     */
    public final List<LogicalBinding<?>> getCallbackBindings() {
        return callbackBindings;
    }

    /**
     * Adds a binding to the service or the reference.
     *
     * @param binding Binding to be added to the service or the reference.
     */
    public final void addBinding(LogicalBinding<?> binding) {
        bindings.add(binding);
    }

    /**
     * Adds a callback binding to the service or the reference.
     *
     * @param binding Binding to be added to the service or the reference.
     */
    public final void addCallbackBinding(LogicalBinding<?> binding) {
        callbackBindings.add(binding);
    }

}
