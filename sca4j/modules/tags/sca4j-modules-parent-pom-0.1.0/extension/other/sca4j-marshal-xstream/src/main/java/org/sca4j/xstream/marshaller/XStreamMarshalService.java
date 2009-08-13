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
