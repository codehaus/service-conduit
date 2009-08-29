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
package org.sca4j.spi.services.contribution;

import java.net.URI;
import java.io.Serializable;

import org.sca4j.scdl.ValidationContext;

/**
 * Implementations store contribution metadata
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public interface MetaDataStore {

    /**
     * Stores the contribution metadata
     *
     * @param contribution the contribution metadata
     * @throws MetaDataStoreException if an error storing the metadata occurs
     */
    void store(Contribution contribution) throws MetaDataStoreException;

    /**
     * Returns the contribution for the given URI
     *
     * @param contributionUri the contribution URI
     * @return the contribution for the given URI or null if not found
     */
    Contribution find(URI contributionUri);


    /**
     * Removes the contribution metadata
     *
     * @param contributionUri the contribution uri
     */
    void remove(URI contributionUri);


    /**
     * Resolves a resource element by its symbol against the entire domain symbol space.
     *
     * @param symbol the symbol used to represent the resource element.
     * @return the resource element or null if not found
     * @throws MetaDataStoreException if an error occurs during resolution
     */
    <S extends Symbol> ResourceElement<S, ?> resolve(S symbol) throws MetaDataStoreException;

    /**
     * Resolves the containing resource for a resource element symbol against the given contribution symbol space.
     *
     * @param uri    the contribution uri
     * @param symbol the symbol used to represent the resource element.
     * @return the resource or null if not found
     */
    public Resource resolveContainingResource(URI uri, Symbol symbol);

    /**
     * Resolves a resource element by its symbol against the given contribution uri.
     *
     * @param contributionUri the contribution URI to resolve against
     * @param type            the class representing the resource
     * @param symbol          the symbol used to represent the resource element.
     * @param context         the context to which validation errors and warnings are reported
     * @return the resource element or null if not found
     * @throws MetaDataStoreException if an error occurs during resolution
     */
    <S extends Symbol, V extends Serializable> ResourceElement<S, V> resolve(URI contributionUri, Class<V> type, S symbol, ValidationContext context)
            throws MetaDataStoreException;

    /**
     * Resolves an import to a matching export
     *
     * @param imprt the import to resolve
     * @return a matching contribution or null
     */
    Contribution resolve(Import imprt);

}
