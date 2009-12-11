package com.travelex.tgbp.transform.xslt;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.sca4j.api.annotation.scope.Composite;

import com.travelex.tgbp.transform.api.TransformationException;
import com.travelex.tgbp.transform.api.TransformationService;

/**
 * XSLT based implementation of the {@link TransformationService}.
 */
@Composite
public class XsltTransformationService extends XsltTransformer implements TransformationService<Source, String, Map<String, Object>> {

    /**
     * {@inheritDoc}
     */
    public String transform(Map<String, Object> context, Source... xmlSources) {
        try {
            if (xmlSources == null || xmlSources.length < 1) {
                return null;
            }

            Source source = xmlSources[0];

            Transformer transformer = getConfiguredTransformer(xmlSources, context);

            StringWriter sw = new StringWriter();
            StreamResult streamResult = new StreamResult(sw);
            transformer.transform(source, streamResult);
            return sw.toString();
        }
        catch (TransformerConfigurationException e) {
            throw new TransformationException("Failed to generate the transformer", e);
        }
        catch (TransformerException e) {
            throw new TransformationException("Failed to complete the transformation", e);
        }
    }
}