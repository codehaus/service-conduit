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
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.fabric.services.lcm;

import java.io.File;
import static java.io.File.separator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.sca4j.fabric.util.FileHelper;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.services.lcm.LogicalComponentStore;
import org.sca4j.spi.services.lcm.StoreException;
import org.sca4j.spi.services.lcm.RecoveryException;
import org.sca4j.spi.services.marshaller.MarshalException;
import org.sca4j.spi.services.marshaller.MarshalService;

/**
 * Default implementation of the LogicalComponentStore that persists the logical domain model to disk. The
 * implementation serializes the domain model using XStream.
 *
 * @version $Rev: 4794 $ $Date: 2008-06-08 18:14:05 +0100 (Sun, 08 Jun 2008) $
 */
@Service(LogicalComponentStore.class)
@EagerInit
public class LogicalComponentStoreImpl implements LogicalComponentStore {
    private File serializedFile;
    private URI domainUri;
    private MarshalService marshalService;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;

    public LogicalComponentStoreImpl(@Reference HostInfo hostInfo,
                                     @Reference MarshalService marshalService,
                                     @Reference XMLFactory factory) throws IOException {
        this.marshalService = marshalService;
        outputFactory = factory.newOutputFactoryInstance();
        inputFactory = factory.newInputFactoryInstance();
        domainUri = hostInfo.getDomain();
        File baseDir  = hostInfo.getBaseDir();
        if (baseDir == null) {
            throw new FileNotFoundException("No base directory found");
        }
        File root = new File(baseDir, "stores" + separator + "assembly");
        FileHelper.forceMkdir(root);
        if (!root.exists() || !root.isDirectory() || !root.canRead()) {
            throw new IOException("The location is not a directory: " + root.getCanonicalPath());
        }
        serializedFile = new File(root, "assembly.ser");
    }

    public void store(LogicalCompositeComponent domain) throws StoreException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(serializedFile);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(fos);
            marshalService.marshall(domain, writer);
        } catch (FileNotFoundException e) {
            throw new StoreException("Error serializing assembly", e);
        } catch (MarshalException e) {
            throw new StoreException("Error serializing assembly", e);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO log exception
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public LogicalCompositeComponent read() throws RecoveryException {
        if (!serializedFile.exists()) {
            // no serialized file, create a new domain
            Composite type = new Composite(null);
            CompositeImplementation impl = new CompositeImplementation();
            impl.setComponentType(type);
            ComponentDefinition<CompositeImplementation> definition =
                    new ComponentDefinition<CompositeImplementation>(domainUri.toString());
            definition.setImplementation(impl);
            return new LogicalCompositeComponent(domainUri, domainUri, definition, null);
        }
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(serializedFile);
            return marshalService.unmarshall(LogicalCompositeComponent.class, inputFactory.createXMLStreamReader(fin));
        } catch (FileNotFoundException e) {
            // should not happen
            throw new AssertionError();
        } catch (MarshalException e) {
            throw new RecoveryException("Error recovering", e);
        } catch (XMLStreamException e) {
            throw new RecoveryException("Error recovering", e);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    // TODO log exception
                    e.printStackTrace();
                }
            }
        }

    }

}
