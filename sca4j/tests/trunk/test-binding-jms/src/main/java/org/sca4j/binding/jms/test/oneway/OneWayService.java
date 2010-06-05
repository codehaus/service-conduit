package org.sca4j.binding.jms.test.oneway;




public interface OneWayService {

    /**
     * Echo Message
     * @param val
     */
    void echo(String val);

}
