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
package org.sca4j.scdl;

import java.lang.reflect.Type;

import javax.xml.namespace.QName;

/**
 * Representation of a user-supplied data type comprising a abstract logical form and a runtime-specific physical form.
 * The logical form describes an abstract type in some arbitrary type system such as XML Schema type or Java Classes. It
 * describes the type of data the user is expecting to use. The physical form describes the representation of that
 * logical data actually used by the runtime. This may describe a Java Object (i.e. the physical form would be the Java
 * Type of that Object typically a Class) or it may describe a surrogate for that Object such as stream.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 * @param <P> the type of identifier for the logical type system used by this DataType (such as an XML QName or Java
 * Class)
 */
public class DataType extends ModelObject {
    
    private final Type javaType;
    private QName xsdType;
    
    public DataType(Type javaType) {
        this.javaType = javaType;
    }
    
    public Type getJavaType() {
        return javaType;
    }

    public QName getXsdType() {
        return xsdType;
    }

    public void setXsdType(QName xsdType) {
        this.xsdType = xsdType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((javaType == null) ? 0 : javaType.hashCode());
        result = prime * result + ((xsdType == null) ? 0 : xsdType.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataType other = (DataType) obj;
        if (javaType == null) {
            if (other.javaType != null)
                return false;
        } else if (!javaType.equals(other.javaType))
            return false;
        if (xsdType == null) {
            if (other.xsdType != null)
                return false;
        } else if (!xsdType.equals(other.xsdType))
            return false;
        return true;
    }

}
