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
package org.sca4j.fabric.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.sca4j.spi.binding.BindingProvider;

/**
 * @version $Revision$ $Date$
 */
public class ConfigurableBindingSelectionStrategyTestCase extends TestCase {

    public void testSelectionOrder() throws Exception {
        ConfigurableBindingSelectionStrategy strategy = new ConfigurableBindingSelectionStrategy();
        List<QName> order = new ArrayList<QName>();
        QName bar = new QName("foo", "bar");
        order.add(bar);
        QName baz = new QName("foo", "baz");
        order.add(baz);
        strategy.setScaBindingOrder(order);

        Map<QName, BindingProvider> providers = new HashMap<QName, BindingProvider>();
        BindingProvider bazProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(bazProvider);
        BindingProvider barProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(barProvider);
        providers.put(baz, bazProvider);
        providers.put(bar, barProvider);

        assertEquals(barProvider, strategy.select(providers));

    }

    public void testNoConfiguredOrderSelection() throws Exception {
        ConfigurableBindingSelectionStrategy strategy = new ConfigurableBindingSelectionStrategy();
        List<QName> order = new ArrayList<QName>();

        strategy.setScaBindingOrder(order);

        QName bar = new QName("foo", "bar");
        QName baz = new QName("foo", "baz");

        Map<QName, BindingProvider> providers = new HashMap<QName, BindingProvider>();
        BindingProvider bazProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(bazProvider);
        BindingProvider barProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(barProvider);
        providers.put(baz, bazProvider);
        providers.put(bar, barProvider);

        assertNotNull(strategy.select(providers));

    }

    public void testBadConfigurationSelectionOrder() throws Exception {
        ConfigurableBindingSelectionStrategy strategy = new ConfigurableBindingSelectionStrategy();
        List<QName> order = new ArrayList<QName>();
        QName nonExistent = new QName("foo", "nonExistent");
        order.add(nonExistent);
        QName bar = new QName("foo", "bar");
        order.add(bar);
        strategy.setScaBindingOrder(order);

        QName baz = new QName("foo", "baz");

        Map<QName, BindingProvider> providers = new HashMap<QName, BindingProvider>();
        BindingProvider bazProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(bazProvider);
        BindingProvider barProvider = EasyMock.createMock(BindingProvider.class);
        EasyMock.replay(barProvider);
        providers.put(baz, bazProvider);
        providers.put(bar, barProvider);

        assertEquals(barProvider, strategy.select(providers));

    }

}
