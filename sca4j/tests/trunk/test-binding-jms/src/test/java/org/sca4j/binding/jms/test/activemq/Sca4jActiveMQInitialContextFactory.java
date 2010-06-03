package org.sca4j.binding.jms.test.activemq;

import java.net.URISyntaxException;
import java.util.Hashtable;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;

public class Sca4jActiveMQInitialContextFactory extends ActiveMQInitialContextFactory {

    @SuppressWarnings("unchecked")
    @Override
    protected ActiveMQConnectionFactory createConnectionFactory(String name, Hashtable environment) throws URISyntaxException {
        if (name.startsWith("xa")) {
            return new ActiveMQXAConnectionFactory("vm://localhost");
        } else {
            return super.createConnectionFactory(name, environment);
        }
    }

}
