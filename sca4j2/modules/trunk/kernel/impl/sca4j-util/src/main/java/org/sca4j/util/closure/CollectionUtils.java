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
package org.sca4j.util.closure;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility for applying closures on collections.
 * 
 * TODO To be moved into a separate module.
 * 
 * @version $Revision$ $Date$
 */
public class CollectionUtils {
    
    private CollectionUtils() {
    }
    
    public static <OBJECT> List<OBJECT> filter(List<OBJECT> source, Closure<OBJECT, Boolean> filter) {
        
        List<OBJECT> result = new ArrayList<OBJECT>();
        
        for (OBJECT object : source) {
            if (filter.execute(object)) {
                result.add(object);
            }
        }
        
        return result;
        
    }
    
    public static <OBJECT> Set<OBJECT> filter(Set<OBJECT> source, Closure<OBJECT, Boolean> filter) {
        
        LinkedHashSet<OBJECT> result = new LinkedHashSet<OBJECT>();
        
        for (OBJECT object : source) {
            if (filter.execute(object)) {
                result.add(object);
            }
        }
        
        return result;
        
    }
    
    public static <SOURCE, RESULT> Set<RESULT> transform(Set<SOURCE> source, Closure<SOURCE, RESULT> transformer) {
        
        LinkedHashSet<RESULT> result = new LinkedHashSet<RESULT>();
        
        for (SOURCE object : source) {
            result.add(transformer.execute(object));
        }
        
        return result;
        
    }

}
