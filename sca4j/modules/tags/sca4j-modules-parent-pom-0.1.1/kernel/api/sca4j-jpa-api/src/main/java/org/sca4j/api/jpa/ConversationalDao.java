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
package org.sca4j.api.jpa;

import java.util.List;

import javax.persistence.LockModeType;

import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

/**
 * SCA4J Conversational DAO.
 * 
 * @version $Revision$ $Date$
 */
@Conversational
public interface ConversationalDao<ENTITY, KEY> {
    
    /**
     * Merges the state of the entity.
     * @param entity Entity to be merged.
     */
    ENTITY merge(ENTITY entity);
    
    /**
     * Persists the state of the entity.
     * @param entity Entity to be persisted.
     */
    void persist(ENTITY entity);
    
    /**
     * Refreshes the state of the entity.
     * @param entity Entity to be refreshed.
     */
    void refresh(ENTITY entity);
    
    /**
     * Removes the entity.
     * @param entity Entity to be removed.
     */
    void remove(ENTITY entity);
    
    /**
     * Finds an entity by primary key.
     * @param clazz Entity clazz.
     * @param key Primary key of the entity.
     * @return Entity instance.
     */
    ENTITY findById(Class<ENTITY> clazz, KEY key);
    
    /**
     * Finds entities defined by the named query.
     * @param namedQuery Named query to use.
     * @param clazz Entity clazz.
     * @param args arguments to the query.
     * @return Entity instance.
     */
    List<ENTITY> findByNamedQuery(String namedQuery, Class<ENTITY> clazz, Object ... args);
    
    /**
     * Flushes the current entity manager.
     */
    void flush();
    
    /**
     * Locks the current entity.
     * 
     * @param entity Entity to be locked.
     * @param lockModeType Lock mode type.
     */
    void lock(ENTITY entity, LockModeType lockModeType);
    
    /**
     * Ends the conversation.
     */
    @EndsConversation void close();

}
