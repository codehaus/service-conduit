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
 * Original Codehaus Header
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
 * Original Apache Header
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.idl.wsdl.processor;

import java.net.URI;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.services.contribution.Contribution;

/**
 * @version $Revision: 2694 $ $Date: 2008-02-06 18:08:27 +0000 (Wed, 06 Feb 2008) $
 */
public class WsdlResourceProcessorTest extends TestCase {
    
    private WsdlResourceProcessor wsdlProcessor = new WsdlResourceProcessor();

    /**
     * Checks for version 1.1
     * @throws ContributionException 
     */
    public void testGetOperations() throws ContributionException {    
        
        Contribution contribution = new Contribution(URI.create("test"));
        URL url = getClass().getClassLoader().getResource("example_1_1.wsdl");
        
        wsdlProcessor.index(contribution, url, new DefaultValidationContext());
        wsdlProcessor.process(contribution.getUri(), contribution.getResources().get(0), new DefaultValidationContext(), getClass().getClassLoader());
        
        PortTypeResourceElement resourceElement = contribution.getResources().get(0).getResourceElements(PortTypeResourceElement.class).get(0);
        List<Operation<?>> operations = resourceElement.getOperations();
        assertEquals(1, operations.size());
        
        Operation<XmlSchemaType> operation = (Operation<XmlSchemaType>) operations.get(0);
        assertEquals("GetLastTradePrice", operation.getName());
        
        DataType<List<DataType<XmlSchemaType>>> inputType = operation.getInputType();
        List<DataType<XmlSchemaType>> inputParts = inputType.getLogical();
        assertEquals(1, inputParts.size());
        
        DataType<XmlSchemaType> inputPart = inputParts.get(0);
        XmlSchemaType inputPartLogical = inputPart.getLogical();
        
        assertNotNull(inputPartLogical);
        assertEquals("string", inputPartLogical.getName());
        
        DataType<XmlSchemaType> outputType = operation.getOutputType();
        assertEquals("float", outputType.getLogical().getName());

    }

}
