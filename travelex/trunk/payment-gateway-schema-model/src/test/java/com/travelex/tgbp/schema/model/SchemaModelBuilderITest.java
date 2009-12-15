package com.travelex.tgbp.schema.model;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.schema.model.api.SchemaModelBuilder;

public class SchemaModelBuilderITest extends TestCase {

    @Reference protected SchemaModelBuilder schemaModelBuilder;

    public void testPain001Generation() throws Exception {
        String model = schemaModelBuilder.getSchemaDataModel("pain.001.001.03");
        assertNotNull(model);
        //System.out.println("\n\npain.001.001.03.xsd=" + model + "\n\n");
    }

    public void testPacs008Generation() throws Exception {
        String model = schemaModelBuilder.getSchemaDataModel("pacs.008.001.02");
        assertNotNull(model);
        //System.out.println("\n\npacs.008.001.02.xsd=" + model + "\n\n");
    }

}
