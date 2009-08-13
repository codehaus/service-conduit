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
package org.sca4j.jaxb.runtime.impl;

import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.AbstractPullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;

/**
 *  Transforms from an XML string representation to a JAXB object.
 *
 * @version $Revision$ $Date$
 */

/**
 * Transforms XML types to a JAXB object.
 *
 * @version $Revision$ $Date$
 */
public class Xml2JAXBTransformer extends AbstractPullTransformer<String, Object> {
    private static final JavaClass<String> TARGET = new JavaClass<String>(String.class);

    private final JAXBContext jaxbContext;

    public Xml2JAXBTransformer(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public Object transform(String source, TransformContext context) throws TransformationException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(source);
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new TransformationException(e);
        }
    }

    public DataType<?> getTargetType() {
        return TARGET;
    }
}
