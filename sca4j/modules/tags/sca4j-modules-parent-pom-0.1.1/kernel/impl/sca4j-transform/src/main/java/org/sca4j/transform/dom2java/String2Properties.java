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
package org.sca4j.transform.dom2java;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Properties;

import org.w3c.dom.Node;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class String2Properties extends AbstractPullTransformer<Node, Properties> {
    private static final JavaClass<Properties> TARGET = new JavaClass<Properties>(Properties.class);

    public DataType<?> getTargetType() {
        return TARGET;
    }

    public Properties transform(Node node, TransformContext context) throws TransformationException {
        String content = node.getTextContent();
        Properties properties = new Properties();
        try {
            properties.load(new StringBufferInputStream(content));
        } catch (IOException e) {
            throw new TransformationException(e);
        }
        return properties;
    }
}
