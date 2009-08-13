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
package org.sca4j.binding.http.runtime.invocation.security;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * SCA4j HttpConnection Manager used to override default host configuration settings when connecting over SSL,
 * This is being used in replacement of {@link Protocol.registerProtocol}
 */
public class SCA4jHttpConnectionManager extends SimpleHttpConnectionManager {
	
    private HostConfiguration hostConfiguration;
	
    /**
     * Default Constructor initialized by the given host configuration
     * @param hostConfiguration
     */
    SCA4jHttpConnectionManager(HostConfiguration hostConfiguration){
		this.hostConfiguration = hostConfiguration;
	}
	
   /**
    * Delegates to the underlying superclass with its own host configuration provided
    * at initialization time
    */
   public HttpConnection getConnection(HostConfiguration hostConfiguration){
    	return super.getConnection(this.hostConfiguration);
    }

    /**
     * Delegates to the underlying superclass with its own host configuration
     */
    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout)  {
    	return super.getConnectionWithTimeout(this.hostConfiguration, timeout);
    }

}
