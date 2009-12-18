package com.travelex.tgbp.schema.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.osoa.sca.annotations.Reference;
import org.xml.sax.SAXException;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.parser.XSOMParser;
import com.travelex.tgbp.schema.model.api.SchemaModelBuildException;
import com.travelex.tgbp.schema.model.api.SchemaModelBuilder;
import com.travelex.tgbp.transform.api.TransformationService;

public class DefaultSchemaModelBuilder implements SchemaModelBuilder {

    @Reference protected TransformationService<Source, String, Map<String, Object>> transformer;

    public String getSchemaDataModel(String schemaName) {
        try {
            String schemaResourceName = "xsd/" + schemaName + ".xsd";
            Source schemaSource = getAsSource(schemaResourceName);
            Source baseSchemaTypeToJavaTypeMappings = getAsSource("reference/type-mappings.xml");
            SchemaDescriptor schemaDescriptor = getSchemaDescriptor(schemaResourceName);
            Source schemaTypeToBaseSchemaTypeMappings = new StreamSource(schemaDescriptor.getSchemaTypesToBaseTypesMapping());

            Map<String, Object> context = populateContext(schemaDescriptor);
            String output = transformer.transform(context, schemaSource, schemaTypeToBaseSchemaTypeMappings, baseSchemaTypeToJavaTypeMappings);
            String ret = output.trim();

            System.out.println("\nRETURNING SCHEMA MODEL: " + ret.length() + "\n");

            return ret;
        } catch (SAXException e) {
            throw new SchemaModelBuildException("Failed to build the model for " + schemaName, e);
        }

    }

    private Map<String, Object> populateContext(SchemaDescriptor schemaDescriptor) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("root.element.name", schemaDescriptor.getRootElementName());
        return context;
    }

    private Source getAsSource(String resourceName) {
        InputStream resourceData = readResource(resourceName);
        return new StreamSource(resourceData);
    }

    private InputStream readResource(String resourceName) {
        InputStream resourceData = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if(resourceData == null) {
            throw new SchemaModelBuildException("Failed to find resource " + resourceName);
        }
        return resourceData;
    }

    private SchemaDescriptor getSchemaDescriptor(String schemaFile) throws SAXException  {

        InputStream schemaSource = readResource(schemaFile);
        XSOMParser parser = new XSOMParser(); //From tools.jar
        parser.parse(schemaSource);

        XSSchemaSet sset = parser.getResult();
        Iterator<XSSimpleType> simpleTypes = sset.iterateSimpleTypes();

        Map<String, String> schemaTypesToBaseTypes = new HashMap<String, String>();

        while(simpleTypes.hasNext()) {
            XSSimpleType type = simpleTypes.next();

            if(type.getBaseType() != null && type.getBaseType().getName() != null) {
                if (type.getBaseType().getName().equals("anySimpleType")) {
                    schemaTypesToBaseTypes.put(type.getName(), type.getName());
                }
                else {
                    schemaTypesToBaseTypes.put(type.getName(), type.getBaseType().getName());
                }
            }
        }

        XSSchema schema = null;
        Collection<XSSchema> schemas = sset.getSchemas();
        for (XSSchema xsSchema : schemas) {
            if(!"http://www.w3.org/2001/XMLSchema".equals(xsSchema.getTargetNamespace()))
            {
                schema = xsSchema;
                break;
            }
        }

        //Assumes single top level element declaration (true of ISO schemas - may need to make root element user definable for other types)
        Iterator<XSElementDecl> elementDecls = schema.iterateElementDecls();
        XSElementDecl rootElement = elementDecls.next();

        return new SchemaDescriptor(rootElement.getName(), schemaTypesToBaseTypes);
    }

    private class SchemaDescriptor {

        private final String rootElementname;
        private final Map<String, String> schemaTypesToBaseTypes;

        public String getRootElementName() {
            return rootElementname;
        }

        public SchemaDescriptor(String rootElementname, Map<String, String> schemaTypesToBaseTypes) {
            this.rootElementname = rootElementname;
            this.schemaTypesToBaseTypes = schemaTypesToBaseTypes;
        }

        public InputStream getSchemaTypesToBaseTypesMapping() {

            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<mappings>");
            for (Map.Entry<String, String> entry : schemaTypesToBaseTypes.entrySet()) {
                sb.append("<mapping>");
                sb.append("<schemaType>" + entry.getKey() + "</schemaType>");
                sb.append("<baseType>" + entry.getValue() + "</baseType>");
                sb.append("</mapping>");
            }
            sb.append("</mappings>");

            return new ByteArrayInputStream(sb.toString().getBytes());
        }

    }

}
