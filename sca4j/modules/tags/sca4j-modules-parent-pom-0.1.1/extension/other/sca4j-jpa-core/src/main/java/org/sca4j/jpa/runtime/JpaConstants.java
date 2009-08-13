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
package org.sca4j.jpa.runtime;

/**
 *
 * @version $Revision$ $Date$
 */
public class JpaConstants {

    public static final String PROPERTY_VALUE = "value";

    public static final String PROPERTY_NAME = "name";
    
    public static final String NAMED_UNIT = "//persistence-unit[@name=''{0}'']";
    
    public static final String ANY_UNIT = "//persistence-unit";

    public static final String PROPERTY = "/properties/property";

    public static final String TRANSACTION_TYPE = "/@transaction-type";    
    
    public static final String NAME = "/@name";

    public static final String PROVIDER = "/provider";

    public static final String NON_JTA_DATA_SOURCE = "/non-jta-data-source";

    public static final String MAPPING_FILE = "/mapping-file";

    public static final String CLASS = "/class";

    public static final String JTA_DATA_SOURCE = "/jta-data-source";

    public static final String JAR_FILE = "/jar-file";

    public static final String EXCLUDE_UNLISTED_CLASSES = "/exclude-unlisted-classes";

}
