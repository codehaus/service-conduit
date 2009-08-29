/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.idl.wsdl.version;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Default implementation of the WSDL version checker.
 */
public class DefaultWsdlVersionChecker implements WsdlVersionChecker {

    /**
     * @see org.sca4j.idl.wsdl.version.WsdlVersionChecker#getVersion(java.net.URL)
     */
    public WsdlVersion getVersion(URL wsdlUrl) {

        InputStream wsdlStream = null;

        try {

            wsdlStream = wsdlUrl.openConnection().getInputStream();

            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(wsdlStream);
            
            // Get to the document element
            while(reader.next() != START_ELEMENT) {
            }
            String localPart = reader.getName().getLocalPart();
            if ("definitions".equals(localPart)) {
                return WsdlVersion.VERSION_1_1;
            } else if ("description".equals(localPart)) {
                return WsdlVersion.VERSION_2_0;
            } else {
                throw new WsdlVersionCheckerException("Unknown document element " + localPart);
            }
            
        } catch (XMLStreamException ex) {
            throw new WsdlVersionCheckerException("Unable to read stream", ex);
        } catch (IOException ex) {
            throw new WsdlVersionCheckerException("Unable to read stream", ex);
        } finally {
            try {
                if (wsdlStream != null) {
                    wsdlStream.close();
                }
            } catch (IOException ignore) {
            }
        }

    }

}
