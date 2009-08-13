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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WeatherResponse {
    
    private WeatherCondition condition;
    private double temperatureMinimum;
    private double temperatureMaximum;
    
    public WeatherCondition getCondition() {
        return condition;
    }
    
    public void setCondition(WeatherCondition condition) {
        this.condition = condition;
    }
    
    public double getTemperatureMinimum() {
        return temperatureMinimum;
    }
    
    public void setTemperatureMinimum(double temperatureMinimum) {
        this.temperatureMinimum = temperatureMinimum;
    }
    
    public double getTemperatureMaximum() {
        return temperatureMaximum;
    }
    
    public void setTemperatureMaximum(double temperatureMaximum) {
        this.temperatureMaximum = temperatureMaximum;
    }

}
