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
package org.sca4j.api.annotation.logging;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
* Defines logging levels recognised by the {@link LogLevel} annotation.
* The log levels supported by the logging implementation underlying any given
* monitor factory implementation may not match the levels defined here and so
* monitor factories may be required to carry out a mapping between levels 
**/  
public enum LogLevels {

    SEVERE,
    
    WARNING,
    
    INFO,
    
    CONFIG,
    
    FINE,
    
    FINER,
    
    FINEST;
    
    /**
     * Encapsulates the logic used to read monitor method log level annotations. 
     * Argument <code>Method</code> instances should be annotated with a {@link LogLevel} directly
     * or with one of the level annotations which have a {@link LogLevel} meta-annotation.  
     * @param the annotated monitor method  
     * @return the annotated <code>LogLevels</code> value 
     */
    public static LogLevels getAnnotatedLogLevel(Method method) {
        LogLevels level = null;
        
        LogLevel annotation = method.getAnnotation(LogLevel.class);
        if (annotation != null) {
            level = annotation.value();
        }
        
        if (level == null) {
            for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
                
                LogLevel logLevel = annotationType.getAnnotation(LogLevel.class);
                if (logLevel != null) {
                    level = logLevel.value();
                    break;
                }
            }            
        }
        
        return level;
    }  
    
}
