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
package org.sca4j.ftp.server.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import org.oasisopen.sca.annotation.Property;

/**
 * 
 * User manager implementation that reads the credential from the file system.
 *
 * @version $Revision$ $Date$
 */
public class FileSystemUserManager implements UserManager {
    
    private Properties users = new Properties();
    
    /**
     * Logins a user using user name and password.
     * 
     * @param user Name of the user.
     * @param password Password for the user.
     * @return True if the user name and password are valid.
     */
    public boolean login(String user, String password) {
        return users.containsKey(user) && password.equals(users.get(user));
    }
    
    /**
     * Login a user using X509 certificate.
     * 
     * @param certificate Certificate of the logging in user.
     * @return True if the user name and password are valid.
     */
    public boolean login(X509Certificate certificate) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Sets the users and passwords as a map.
     * 
     * @param users Map of users to passwords.
     */
    @Property(required=false)
    public void setUsers(Map<String, String> users) {
        this.users.putAll(users);
    }
    
    /**
     * Loads the users and passwords from a file.
     * 
     * @param userFile User file location.
     * @throws IOException If unable to load the properties.
     */
    @Property(required=false)
    public void setUserFile(String userFile) throws IOException {
        
        InputStream inputStream = new FileInputStream(userFile);
        try {
            users.load(inputStream);
        } finally {
            inputStream.close();
        }
        
    }

}
