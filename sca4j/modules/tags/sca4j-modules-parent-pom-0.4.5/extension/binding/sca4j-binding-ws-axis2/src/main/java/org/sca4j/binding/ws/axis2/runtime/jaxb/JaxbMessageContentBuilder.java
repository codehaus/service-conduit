package org.sca4j.binding.ws.axis2.runtime.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class JaxbMessageContentBuilder {

    private final Method interceptedMethod;
    private final Object[] payload;
    private final Jaxb2OMElement outTransformer;


    public JaxbMessageContentBuilder(Method interceptedMethod, JAXBContext jaxbContext, Object[] payload) {
        this.interceptedMethod = interceptedMethod;
        this.payload = payload;
        this.outTransformer = new Jaxb2OMElement(jaxbContext);
    }

    public List<MessageData> build() {

        int index = 0;
        Annotation[][] parameterAnnotations = interceptedMethod.getParameterAnnotations();
        Class<?>[] parameterTypes = interceptedMethod.getParameterTypes();

        List<MessageData> messageContent = new ArrayList<MessageData>();
        for (Object jaxbObject : payload) {
            boolean header = false;
            Class<? extends Object> payloadItemClass = jaxbObject.getClass();
            Class<?> parameterType = parameterTypes[index];

            Annotation[] annotations = parameterAnnotations[index];
            for (Annotation annotation : annotations) {
                if(annotation.annotationType().equals(WebParam.class)) {
                    WebParam webParam = (WebParam)annotation;
                    header = webParam.header();
                    if(! payloadItemClass.equals(parameterType)) {
                        //Actual payload type must be a subclass of the method parameter type rather than the declared type.
                        //This requires a different mapping configuration for JAXB.
                        String name = webParam.name();
                        String targetNamespace = webParam.targetNamespace();

                        QName qname = new QName(targetNamespace, name);
                        @SuppressWarnings("unchecked")
                        JAXBElement element = new JAXBElement(qname, parameterType, null, jaxbObject);
                        jaxbObject = element;
                    }
                }
            }

            OMElement omElement = outTransformer.transform(jaxbObject, null);
            messageContent.add(new MessageData(omElement, header));

            index++;
        }

        return messageContent;
    }

}
