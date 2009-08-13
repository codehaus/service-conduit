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

import java.net.URI;
import java.net.URISyntaxException;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

import org.w3c.dom.Node;

/**
 * String to URI Transformer
 */
public class String2URI extends AbstractPullTransformer<Node, URI> {
    private static final JavaClass<URI> TARGET = new JavaClass<URI>(URI.class);

    /**
     * @see org.sca4j.transform.Transformer#getTargetType()
     */
    public DataType<?> getTargetType() {
        return TARGET;
    }

    /**
     * Transformation for URI
     * @see org.sca4j.transform.PullTransformer#transform(java.lang.Object, org.sca4j.transform.TransformContext)
     */
    public URI transform(final Node node, final TransformContext context) throws TransformationException {
    	final String content = node.getTextContent();
    	final URI uri;
        try {
			uri = new URI(node.getTextContent());
		} catch (URISyntaxException ue) {
			throw new TransformationException("Unable to create URI :- " + content, ue);
		}
		return uri;
    }
}
