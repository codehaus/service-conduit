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
