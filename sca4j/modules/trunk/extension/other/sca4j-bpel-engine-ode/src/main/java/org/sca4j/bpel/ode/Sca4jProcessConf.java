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
package org.sca4j.bpel.ode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.ode.bpel.compiler.BpelC;
import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.evt.BpelEvent.TYPE;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Sca4jProcessConf implements ProcessConf {
    
    private BpelPhysicalComponentDefinition componentDefinition;
    private File processLocation;
    private File cbpFile;
    private Map<String, Endpoint> invokeEndpoints;
    private Map<String, Endpoint> provideEndpoints;

    public Sca4jProcessConf(BpelPhysicalComponentDefinition componentDefinition) throws URISyntaxException, CompilationException, IOException {
        
        this.componentDefinition = componentDefinition;
        this.processLocation = new File(componentDefinition.getProcessUrl().toURI());
        
        BpelC compiler = BpelC.newBpelCompiler();
        compiler.setBaseDirectory(processLocation.getParentFile());
        compiler.compile(processLocation);
        
        cbpFile = new File(processLocation.getAbsolutePath().replace(".bpel", ".cbp"));
        
    }

    @Override
    public URI getBaseURI() {
        return processLocation.getParentFile().toURI();
    }

    @Override
    public String getBpelDocument() {
        return processLocation.getAbsolutePath();
    }

    @Override
    public long getCBPFileSize() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(cbpFile);
            return IOUtils.toByteArray(inputStream).length;
        } catch (IOException e) {
            throw new Sca4jOdeException("Uneble to read compiled BPEL process " + cbpFile, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public InputStream getCBPInputStream() {
        try {
            return new FileInputStream(cbpFile);
        } catch (IOException e) {
            throw new Sca4jOdeException("Uneble to read compiled BPEL process " + cbpFile, e);
        }
    }

    @Override
    public Set<CLEANUP_CATEGORY> getCleanupCategories(boolean instanceSucceeded) {
        return EnumSet.noneOf(CLEANUP_CATEGORY.class);
    }

    @Override
    public Date getDeployDate() {
        return new Date();
    }

    @Override
    public String getDeployer() {
        return "Service Conduit";
    }

    @Override
    public Map<String, String> getEndpointProperties(EndpointReference endpointReference) {
        return Collections.emptyMap();
    }

    @Override
    public List<Element> getExtensionElement(QName qualifiedName) {
        return Collections.emptyList();
    }

    @Override
    public List<File> getFiles() {
        return Arrays.asList(processLocation.getParentFile().listFiles());
    }

    @Override
    public Map<String, Endpoint> getInvokeEndpoints() {
        if (invokeEndpoints == null) {
            invokeEndpoints = new HashMap<String, Endpoint>();
            for (Map.Entry<String, QName> reference : componentDefinition.getReferenceEndpoints().entrySet()) {
                String partnerLinkName = reference.getKey();
                QName portTypeName = reference.getValue();
                Endpoint endpoint = new Endpoint(portTypeName, portTypeName.getLocalPart());
                invokeEndpoints.put(partnerLinkName, endpoint);
            }
        }
        return invokeEndpoints;
    }

    @Override
    public String getPackage() {
        return processLocation.getParentFile().getName();
    }

    @Override
    public QName getProcessId() {
        return componentDefinition.getProcessName();
    }

    @Override
    public Map<QName, Node> getProcessProperties() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Endpoint> getProvideEndpoints() {
        if (provideEndpoints == null) {
            provideEndpoints = new HashMap<String, Endpoint>();
            for (Map.Entry<String, QName> service : componentDefinition.getServiceEndpoints().entrySet()) {
                String partnerLinkName = service.getKey();
                QName portTypeName = service.getValue();
                Endpoint endpoint = new Endpoint(portTypeName, portTypeName.getLocalPart());
                provideEndpoints.put(partnerLinkName, endpoint);
            }
        }
        return provideEndpoints;
    }

    @Override
    public ProcessState getState() {
        return ProcessState.ACTIVE;
    }

    @Override
    public QName getType() {
        return componentDefinition.getProcessName();
    }

    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public boolean isCleanupCategoryEnabled(boolean instanceSucceeded, CLEANUP_CATEGORY cleanupCategory) {
        return false;
    }

    @Override
    public boolean isEventEnabled(List<String> scopeNames, TYPE type) {
        return false;
    }

    @Override
    public boolean isSharedService(QName serviceName) {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public Definition getDefinitionForPortType(QName portTypeName) {
        return componentDefinition.getPortTypes().get(portTypeName);
    }

    @Override
    public Definition getDefinitionForService(QName serviceName) {
        return null;
    }

}
