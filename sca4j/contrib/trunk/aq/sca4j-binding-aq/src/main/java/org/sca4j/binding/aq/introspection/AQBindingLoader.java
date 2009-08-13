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
package org.sca4j.binding.aq.introspection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.binding.aq.common.InitialState;
import org.sca4j.binding.aq.scdl.AQBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;
import org.osoa.sca.Constants;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision: 4817 $ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
@EagerInit
public class AQBindingLoader implements TypeLoader<AQBindingDefinition> {

    /** Qualified name for the binding element. */
    public static final QName BINDING_QNAME  =  new QName(Constants.SCA_NS, "binding.aq");  

    private final LoaderHelper loaderHelper;  
              

    /**
     * Constructor
     * @param LoaderHelper
     */
    public AQBindingLoader(final @Reference LoaderHelper loaderHelper) {                  
          this.loaderHelper = loaderHelper;          
    }
    
    /**
     * @throws XMLStreamException 
     * 
     */
    public AQBindingDefinition load(final XMLStreamReader reader, final IntrospectionContext loaderContext) throws XMLStreamException {                
        System.err.println("Inside The Loader");
        final String destinationName = reader.getAttributeValue(null, "destinationName");
        final String sInitialState = reader.getAttributeValue(null, "initialState");
        final String sConsumerCount = reader.getAttributeValue(null, "consumerCount");
        final String dataSourceKey = reader.getAttributeValue(null, "dataSourceKey");
        
        final int consumerCount = sConsumerCount != null ? Integer.parseInt(sConsumerCount) : 0;
        final InitialState initialState = sConsumerCount != null ? InitialState.valueOf(sInitialState) : InitialState.STARTED;
        
        final AQBindingDefinition bindingDefinition = 
            new AQBindingDefinition(destinationName, initialState, dataSourceKey, consumerCount);
        
        loaderHelper.loadPolicySetsAndIntents(bindingDefinition, reader, loaderContext);
        
        LoaderUtil.skipToEndElement(reader);
        
        return bindingDefinition;

    }
    
}
