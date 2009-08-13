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

import java.net.MalformedURLException;
import java.net.URL;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

import org.w3c.dom.Node;

/**
 * String to URL Transformer
 */
public class String2URL extends AbstractPullTransformer<Node, URL> {
	
	private static final JavaClass<URL> TARGET = new JavaClass<URL>(URL.class);

	/**
	 * @see org.sca4j.transform.Transformer#getTargetType()
	 */
	public DataType<?> getTargetType() {
		return TARGET;
	}

	/**
	 * Transformation for URL
	 * 
	 * @see org.sca4j.transform.PullTransformer#transform(java.lang.Object, org.sca4j.transform.TransformContext)
	 */
	public URL transform(final Node node, final TransformContext context) throws TransformationException {
		final String content = node.getTextContent();
		final URL url;
		try {
			url = new URL(node.getTextContent());
		} catch (MalformedURLException me) {
			throw new TransformationException("Unable to create URL :- " + content, me);
		}
		return url;
	}
}
