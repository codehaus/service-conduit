/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 */
package org.sca4j.management;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.host.management.ManagedAttribute;
import org.sca4j.host.management.ManagementService;
import org.sca4j.host.management.ManagementUnit;
import org.sca4j.spi.host.ServletHost;

/**
 * Default implementation of the management service.
 * 
 * @author meerajk
 *
 */
@EagerInit
public class ManagementServiceImpl extends HttpServlet implements ManagementService {
    
    @Reference public ServletHost servletHost;
    
    private Map<URI, ManagementUnit> managementUnits = new HashMap<URI, ManagementUnit>();
    
    /**
     * Registers the management servlet.
     */
    @Init
    public void start() {
        servletHost.registerMapping("/management/*", this);
    }

    /**
     * Clears an stats associated with a specific management unit.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    /**
     * Gets the current state of the management unit.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        PrintWriter printWriter = resp.getWriter();
        if (pathInfo == null) {
            printWriter.println("Service Conduit Management Domain");
            printWriter.println("---------------------------------");
            for (Map.Entry<URI, ManagementUnit> managementUnit : managementUnits.entrySet()) {
                printWriter.println(managementUnit.getKey() + ":" + managementUnit.getValue().getDescription());
            }
            printWriter.println("---------------------------------");
        } else {
            ManagementUnit managementUnit = managementUnits.get(URI.create(pathInfo));
            if (managementUnit == null) {
                printWriter.println("Requested resource not found");
            } else {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(managementUnit.getClass());
                    printWriter.println(managementUnit.getDescription());
                    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                        Method readMethod = propertyDescriptor.getReadMethod();
                        if (readMethod == null) {
                            continue;
                        }
                        ManagedAttribute managedAttribute = readMethod.getAnnotation(ManagedAttribute.class);
                        if (managedAttribute == null) {
                            continue;
                        }
                            Object value = readMethod.invoke(managementUnit);
                            printWriter.println(propertyDescriptor.getName() + " (" + managedAttribute.value() + "): " + value);
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }                        
            }
            printWriter.flush();
            printWriter.close();
        }
        
    }

    /**
     * Changes the state of the management unit.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        PrintWriter printWriter = resp.getWriter();
        if (pathInfo != null) {
            ManagementUnit managementUnit = managementUnits.get(URI.create(pathInfo));
            if (managementUnit == null) {
                printWriter.println("Requested resource not found");
            } else {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(managementUnit.getClass());
                    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                        Method readMethod = propertyDescriptor.getReadMethod();
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        if (readMethod == null || writeMethod == null) {
                            continue;
                        }
                        ManagedAttribute managedAttribute = readMethod.getAnnotation(ManagedAttribute.class);
                        if (managedAttribute == null) {
                            continue;
                        }
                        PropertyEditor propertyEditor = PropertyEditorManager.findEditor(propertyDescriptor.getPropertyType());
                        String parameter = req.getParameter(propertyDescriptor.getName());
                        if (parameter == null) {
                            continue;
                        }              
                        propertyEditor.setAsText(parameter);
                        Object value = propertyEditor.getValue();
                        writeMethod.invoke(managementUnit, value);
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }
                doGet(req, resp);
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(URI uri, ManagementUnit managementUnit) {
        managementUnits.put(uri, managementUnit);
    }

}
