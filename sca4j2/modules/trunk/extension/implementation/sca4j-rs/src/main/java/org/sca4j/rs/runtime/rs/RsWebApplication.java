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

package org.sca4j.rs.runtime.rs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 5452 $ $Date: 2008-09-20 11:40:07 +0100 (Sat, 20 Sep 2008) $
 */
public final class RsWebApplication extends HttpServlet {

    private static final long serialVersionUID = -5105879427224562209L;
    
    private RsServlet servlet;
    private ClassLoader cl;
    private ServletConfig cfg;
    private SCA4JComponentProviderFactory providerFactory;
    boolean reload = false;
    private ReentrantReadWriteLock reloadRWLock = new ReentrantReadWriteLock();
    private Lock reloadLock = reloadRWLock.readLock();
    private Lock serviceLock = reloadRWLock.writeLock();

    public RsWebApplication(ClassLoader cl) {
        this.cl = cl;
        this.providerFactory = new SCA4JComponentProviderFactory();
        reload = true;
    }

    public void addServiceHandler(Class<?> resource, Object instance) {
        providerFactory.addServiceHandler(resource, instance);
        reload = true;
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        cfg = new ServletConfigWrapper(config);
    }

    public void reload() throws ServletException {
        
        try {
            reloadLock.lock();
            ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
            
            try {
                Thread.currentThread().setContextClassLoader(cl);
                this.servlet = new RsServlet(this.providerFactory);
                servlet.init(cfg);
            } catch (ServletException se) {
                se.printStackTrace();
                throw se;
            } catch (Throwable t) {
                t.printStackTrace();
                throw new ServletException(t);
            } finally {
                Thread.currentThread().setContextClassLoader(oldcl);
            }
            reload = false;
            
        } finally {
            reloadLock.unlock();
        }
        
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        try {
            
            serviceLock.lock();
            if (reload) {
                reload();
            }

            ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
            WorkContext oldContext = null;
            try {
                Thread.currentThread().setContextClassLoader(cl);
                WorkContext workContext = new WorkContext();
                CallFrame frame = new CallFrame();
                workContext.addCallFrame(frame);
                oldContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
                servlet.service(req, res);
            } catch (ServletException se) {
                se.printStackTrace();
                throw se;
            } catch (IOException ie) {
                ie.printStackTrace();
                throw ie;
            } catch (Throwable t) {
                t.printStackTrace();
                ServletException se = new ServletException(t);
                throw se;
            } finally {
                Thread.currentThread().setContextClassLoader(oldcl);
                PojoWorkContextTunnel.setThreadWorkContext(oldContext);
            }
            
        } finally {
            serviceLock.unlock();
        }
        
    }

    /**
     * 
     * Wrapper class to add the Jersey resource class as a web app init
     * parameter
     * 
     */
    public class ServletConfigWrapper implements ServletConfig {

        ServletConfig config;

        public ServletConfigWrapper(ServletConfig config) {
            this.config = config;
        }

        public String getInitParameter(String name) {
            if ("javax.ws.rs.Application".equals(name)) {
                return SCA4JResourceConfig.class.getName();
            }
            return config.getInitParameter(name);
        }

        @SuppressWarnings("unchecked")
		public Enumeration getInitParameterNames() {
            final Enumeration e = config.getInitParameterNames();
            return new Enumeration() {

                boolean finished = false;

                public boolean hasMoreElements() {
                    if (e.hasMoreElements() || !finished) {
                        return true;
                    }
                    return false;
                }

                public Object nextElement() {
                    if (e.hasMoreElements()) {
                        return e.nextElement();
                    }
                    if (!finished) {
                        finished = true;
                        return "javax.ws.rs.Application";
                    }
                    return null;
                }
            };
        }

        public ServletContext getServletContext() {
            return config.getServletContext();
        }

        public String getServletName() {
            return config.getServletName();
        }
    }

    
}
