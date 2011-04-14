package org.sca4j.binding.http.runtime.invocation.security;

import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.sca4j.binding.http.provision.security.AuthenticationPolicy;

public abstract class AbstractConnectionProvider<T extends AuthenticationPolicy> implements ConnectionProvider<T> {

	/**
	 * {@inheritDoc}
	 */
	public HttpClient createClient(T authenticationPolicy, URL url, URI classLoaderId) {		
		return getHttpClient(new SCA4jHttpConnectionManager(buildHostInfo(authenticationPolicy, url)));
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpClient createClient(T authenticationPolicy, URL url, URI classLoaderId, long timeout) {
		final SCA4jHttpConnectionManager scaConnMgr = new SCA4jHttpConnectionManager(buildHostInfo(authenticationPolicy, url));
		final HttpConnectionManagerParams  params = new HttpConnectionManagerParams();		
		params.setConnectionTimeout((int)timeout);
		params.setSoTimeout((int)timeout);									
		scaConnMgr.setParams(params);
		
		return getHttpClient(scaConnMgr);
		
	}
	
	/**
	 * Builds the correct host info for the given HTTP connection 
	 * @param authPolicy - policy data for authentication 
	 * @param url - url destination
	 * @return host Configuration contextual information required for the http binding 
	 */
	abstract HostConfiguration buildHostInfo(T authPolicy, URL url);
	
	/*
	 * Creates the http client
	 */
	private HttpClient getHttpClient(SCA4jHttpConnectionManager connManager) {		           
		final HttpClient httpClient = new HttpClient(connManager);		
		httpClient.getParams().setConnectionManagerClass(connManager.getClass());		
		return httpClient;
	}
}
