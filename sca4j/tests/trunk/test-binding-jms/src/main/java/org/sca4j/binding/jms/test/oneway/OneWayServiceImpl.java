package org.sca4j.binding.jms.test.oneway;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.api.annotation.logging.Info;

@Scope("COMPOSITE")
@Service(value = { OneWayService.class, OneWayServiceImpl.class })
public class OneWayServiceImpl implements OneWayService {

    @Monitor protected PrintOut mon;

    private String value;

    /**
     * {@inheritDoc}
     */
    @Override
    public void echo(String val) { value = val; mon.mssg("Info Sent : " + value);  }

    /**
     * {@inheritDoc}
     */
    public String getEchoVal() { return value; }

    public interface PrintOut { @Info void mssg(String msg); }

}
