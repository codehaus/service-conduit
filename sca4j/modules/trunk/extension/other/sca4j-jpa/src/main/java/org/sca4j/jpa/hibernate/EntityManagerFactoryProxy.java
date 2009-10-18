package org.sca4j.jpa.hibernate;

import java.util.Collections;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.ejb.Ejb3Configuration;
import org.sca4j.host.perf.PerformanceMonitor;

public class EntityManagerFactoryProxy implements EntityManagerFactory {
	
	private EntityManagerFactory delegate;
	private PersistenceUnitInfo info;
	private Ejb3Configuration config;
	
	/**
	 * @param info
	 * @param config
	 */
	public EntityManagerFactoryProxy(PersistenceUnitInfo info, Ejb3Configuration config) {
		this.info = info;
		this.config = config;
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#close()
	 */
	public void close() {
		getDelegate().close();
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#createEntityManager()
	 */
	public EntityManager createEntityManager() {
		return getDelegate().createEntityManager();
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#createEntityManager(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public EntityManager createEntityManager(Map arg0) {
		return getDelegate().createEntityManager(arg0);
	}

	/**
	 * @see javax.persistence.EntityManagerFactory#isOpen()
	 */
	public boolean isOpen() {
		return getDelegate().isOpen();
	}
	
	private synchronized EntityManagerFactory getDelegate() {
		if (delegate == null) {
	        PerformanceMonitor.start("Proxied EJB3 configuration " + info.getPersistenceUnitName());
	        config.configure(info, Collections.emptyMap());
	        PerformanceMonitor.end();
	        PerformanceMonitor.start("Building proxied entity manager factory " + info.getPersistenceUnitName());
	        delegate = config.buildEntityManagerFactory();
	        PerformanceMonitor.end();
		}
		return delegate;
	}

}
