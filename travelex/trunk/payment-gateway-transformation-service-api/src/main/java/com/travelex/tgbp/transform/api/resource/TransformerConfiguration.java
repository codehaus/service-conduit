package com.travelex.tgbp.transform.api.resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default configuration utility class which returns an unmodifiable map of configuration entries.
 */
public class TransformerConfiguration {
    private final Map<String, Object> config = new HashMap<String, Object>();

    /**
     * Set a configuration entry using the specified String key and Object value.
     * 
     * @param key the key
     * @param value the value
     */
    public void setConfigurationEntry(String key, Object value) {
        config.put(key, value);
    }

    /**
     * Return an unmodifiable map of configuration entries.
     * 
     * @return the configuration entries
     */
    public Map<String, Object> getConfigurationEntries() {
        return Collections.unmodifiableMap(config);
    }
}
