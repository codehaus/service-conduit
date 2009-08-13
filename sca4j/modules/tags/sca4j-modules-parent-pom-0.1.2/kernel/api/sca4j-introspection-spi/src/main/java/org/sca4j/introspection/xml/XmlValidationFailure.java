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
package org.sca4j.introspection.xml;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * Base class for validation failures occuring in XML artifacts.
 *
 * @version $Revision$ $Date$
 */
public abstract class XmlValidationFailure<T> extends ValidationFailure<T> {
    private final int line;
    private final int column;
    private final String message;
    private String resourceURI;

    protected XmlValidationFailure(String message, T modelObject, XMLStreamReader reader) {
        super(modelObject);
        this.message = message;
        Location location = reader.getLocation();
        line = location.getLineNumber();
        column = location.getColumnNumber();
        resourceURI = location.getSystemId();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        if (line != -1) {
            builder.append(" [").append(line).append(',').append(column).append("]");
        }
        return builder.toString();
    }

}
