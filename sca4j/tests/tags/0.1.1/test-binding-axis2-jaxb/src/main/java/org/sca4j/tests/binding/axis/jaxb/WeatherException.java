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
package org.sca4j.tests.binding.axis.jaxb;

import javax.xml.ws.WebFault;

/**
 * @version $Rev: 4099 $ $Date: 2008-05-03 10:13:55 +0100 (Sat, 03 May 2008) $
 */
@WebFault
public class WeatherException extends Exception {

    private static final long serialVersionUID = -5442856978625675327L;
    
    private BadWeatherFault faultInfo;

    public WeatherException(String message, BadWeatherFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public WeatherException(String message, BadWeatherFault faultInfo, Throwable throwable) {
        super(message, throwable);
        this.faultInfo = faultInfo;
    }

    public BadWeatherFault getFaultInfo() {
        return faultInfo;
    }
}
