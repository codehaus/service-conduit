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
package org.sca4j.spi.services.marshaller;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Converts an object to and from an XML representation.
 *
 * @version $Rev: 3015 $ $Date: 2008-03-03 07:50:28 +0000 (Mon, 03 Mar 2008) $
 */
public interface MarshalService {
    /**
     * Marshalls an object.
     *
     * @param object object to be marshalled.
     * @param writer Writer to which marshalled information is written.
     * @throws MarshalException if an error is encountered marshalling
     */
    void marshall(Object object, XMLStreamWriter writer) throws MarshalException;

    /**
     * Unmarshalls an XML stream to an object.
     *
     * @param type   the unmarshalled type
     * @param reader Reader from which marshalled information is read.
     * @return object from the marshalled stream.
     * @throws MarshalException if an error is encountered unmarshalling
     */
    <T> T unmarshall(Class<T> type, XMLStreamReader reader) throws MarshalException;

}
