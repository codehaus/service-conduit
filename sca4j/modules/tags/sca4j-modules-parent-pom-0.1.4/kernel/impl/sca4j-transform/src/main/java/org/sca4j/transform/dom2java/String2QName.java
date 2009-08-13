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

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

/**
 * @version $Rev: 42 $ $Date: 2007-05-16 18:58:55 +0100 (Wed, 16 May 2007) $
 */
public class String2QName extends AbstractPullTransformer<Node, QName> {
    /**
     * Target Class (Long)
     */
    private static final JavaClass<QName> TARGET = new JavaClass<QName>(QName.class);

    /**
     * @see org.sca4j.transform.Transformer#getTargetType()
     */
    public DataType<?> getTargetType() {
        return TARGET;
    }

    /**
     * @see org.sca4j.transform.PullTransformer#transform(java.lang.Object, org.sca4j.transform.TransformContext)
     *      Applies Transformation for QName
     */
    public QName transform(final Node node, final TransformContext context) throws TransformationException {
        String content = node.getTextContent();
        // see if the content looks like it might reference a namespace
        int index = content.indexOf(':');
        if (index != -1) {
            String prefix = content.substring(0, index);
            String uri = node.lookupNamespaceURI(prefix);
            // a prefix was found that resolved to a namespace - return the associated QName
            if (uri != null) {
                String localPart = content.substring(index + 1);
                return new QName(uri, localPart, prefix);
            }
        }
        try {
            return QName.valueOf(content);
        } catch (IllegalArgumentException ie) {
            throw new TransformationException("Unable to transform on QName ", ie);
        }
	}
}
