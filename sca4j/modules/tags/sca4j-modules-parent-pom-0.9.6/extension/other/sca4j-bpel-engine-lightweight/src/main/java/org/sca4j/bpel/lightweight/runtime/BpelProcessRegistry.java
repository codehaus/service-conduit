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
 */
package org.sca4j.bpel.lightweight.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.lightweight.Sca4jBpelException;
import org.sca4j.bpel.lightweight.introspection.BpelProcessIntrospector;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.introspection.xml.LoaderException;

public class BpelProcessRegistry {

    @Reference
    public BpelProcessIntrospector bpelProcessIntrospector;

    private Map<URI, BpelProcessDefinition> definitions = new HashMap<URI, BpelProcessDefinition>();

    public BpelProcessDefinition getDefinition(URI componentId) {
        return definitions.get(componentId);
    }

    public void register(URI componentId, URL processUrl) {

        InputStream inputStream = null;
        try {
            inputStream = processUrl.openStream();
            BpelProcessDefinition bpelProcessDefinition = bpelProcessIntrospector.introspect(inputStream);
            definitions.put(componentId, bpelProcessDefinition);
        } catch (IOException e) {
            throw new Sca4jBpelException("Unable tp parse BPEL process " + processUrl, e);
        } catch (LoaderException e) {
            throw new Sca4jBpelException("Unable tp parse BPEL process " + processUrl, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

}
