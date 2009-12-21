package com.travelex.tgbp.integration;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.schema.model.api.SchemaModelBuilder;

/**
 * Integration test for assembly module.
 */
public class IntegrationITest extends TestCase {

    @Reference protected SchemaModelBuilder schemaModelBuilder;

    /**
     * Tests successful wiring of integration components
     */
    public void testWiring() {
        assertNotNull(schemaModelBuilder);
    }

}
