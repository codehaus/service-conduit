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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

/**
 * @version $Rev: 566 $ $Date: 2007-07-24 22:07:41 +0100 (Tue, 24 Jul 2007) $ String format of Date, expects format of
 *          date as dd/mm/yyyy (<date>12/07/2007</date>)
 */
public class String2Date extends AbstractPullTransformer<Node, Date> {

    /**
     * Standard Date Format
     */
    private final DateFormat dateFormatter;

    /**
     * Target Class (Date)
     */
    private static final JavaClass<Date> TARGET = new JavaClass<Date>(Date.class);

    /**
     * Default Constructor
     */
    public String2Date() {
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        dateFormatter.setLenient(false);
    }

    /**
     * @see org.sca4j.transform.Transformer#getTargetType()
     */
    public DataType<?> getTargetType() {
        return TARGET;
    }

    /**
     * @see org.sca4j.transform.PullTransformer#transform(java.lang.Object, org.sca4j.transform.TransformContext)
     *      Applies transformation for Date
     */
    public Date transform(final Node node, final TransformContext context) throws TransformationException {
        try {
            return dateFormatter.parse(node.getTextContent());
        } catch (ParseException pe) {
            throw new TransformationException("Unsupported Date Format ", pe);
		} 
	}

}
