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
 */
package org.sca4j.spi.resource;


/**
 * @version $Revision$ $Date$
 */
public interface ResourceRegistry {
    
    /**
     * @param resourceType
     * @param name
     * @param resource
     */
    void registerResource(Class<?> resourceType, String name, Object resource);
    
    /**
     * @param <T>
     * @param resourceType
     * @param name
     * @return
     */
    <T> T getResource(Class<T> resourceType, String name);

}
