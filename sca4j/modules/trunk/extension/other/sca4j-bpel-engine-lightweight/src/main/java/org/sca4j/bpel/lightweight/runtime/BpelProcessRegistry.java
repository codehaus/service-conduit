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
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

}
