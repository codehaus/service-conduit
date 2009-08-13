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
package org.sca4j.transform.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import org.sca4j.scdl.DataType;
import org.sca4j.transform.AbstractPushTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class Stream2Element extends AbstractPushTransformer<XMLStreamReader, Element> {
    private final Stream2Stream streamTransformer;


    public Stream2Element(Stream2Stream streamTransformer) {
        this.streamTransformer = streamTransformer;
    }

    public DataType<?> getSourceType() {
        return null;
    }

    public DataType<?> getTargetType() {
        return null;
    }

    public void transform(XMLStreamReader reader, Element element, TransformContext context) throws TransformationException {
        XMLStreamWriter writer = new DOMStreamWriter(element.getOwnerDocument(), element);
        try {
            streamTransformer.transform(reader, writer, null);
        } finally {
            try {
                writer.close();
            } catch (XMLStreamException e) {
                // ignore
            }
        }
    }
}
