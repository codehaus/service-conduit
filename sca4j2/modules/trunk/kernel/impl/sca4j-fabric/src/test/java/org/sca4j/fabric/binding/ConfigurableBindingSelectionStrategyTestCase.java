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
