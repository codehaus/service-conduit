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
package org.sca4j.host.contribution;

import javax.xml.namespace.QName;

/**
 * Definitions of constants.
 *
 * @version $Rev: 4805 $ $Date: 2008-06-09 13:35:29 +0100 (Mon, 09 Jun 2008) $
 */
public final class Constants {
    /**
     * A changeSet represented as XML.
     */
    public static final String CHANGESET_XML = "application/x-sca4j.sca4j.changeSet+xml";
    public static final String ZIP_CONTENT_TYPE = "application/zip";
    public static final String FOLDER_CONTENT_TYPE = "application/vnd.sca4j.folder";
    public static final String COMPOSITE_CONTENT_TYPE = "text/vnd.sca4j.composite+xml";
    public static final String DEFINITIONS_TYPE = "text/vnd.sca4j.definitions+xml";
    public static final String JAVA_CONTENT_TYPE = "application/java-vm";
    public final static String CONTENT_UNKONWN = "content/unknown";
    public final static String CONTENT_DEFAULT = "application/octet-stream";
    
    public static final String URI_PREFIX = "sca://contribution/";

    public final static QName COMPOSITE_TYPE = new QName("http://www.osoa.org/xmlns/sca/1.0", "composite");

    private Constants() {
    }

}
