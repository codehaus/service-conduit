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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.xstream.marshaller;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.services.marshaller.MarshalException;
import org.sca4j.spi.services.marshaller.MarshalService;
import org.sca4j.xstream.factory.ClassLoaderStaxDriver;
import org.sca4j.xstream.factory.XStreamFactory;

/**
 * XStream-based implementation of the MarshalService.
 *
 * @version $Rev: 5284 $ $Date: 2008-08-26 17:54:14 +0100 (Tue, 26 Aug 2008) $
 */
@EagerInit
public class XStreamMarshalService implements MarshalService {
    private XStream xStream;
    private StaxDriver staxDriver;

    public XStreamMarshalService(@Reference XStreamFactory factory) {
        xStream = factory.createInstance();
        staxDriver = new ClassLoaderStaxDriver(getClass().getClassLoader());
    }

    public void marshall(Object modelObject, XMLStreamWriter writer) throws MarshalException {
        try {
            xStream.marshal(modelObject, staxDriver.createStaxWriter(writer));
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
    }

    public <T> T unmarshall(Class<T> type, XMLStreamReader reader) throws MarshalException {
        return type.cast(xStream.unmarshal(staxDriver.createStaxReader(reader)));
    }

}
