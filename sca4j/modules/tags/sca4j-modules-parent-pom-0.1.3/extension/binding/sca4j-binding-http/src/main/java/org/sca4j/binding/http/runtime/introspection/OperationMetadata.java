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
package org.sca4j.binding.http.runtime.introspection;

import java.util.List;

/**
 * Operation metadata.
 * 
 * TODO move this to the generation phase.
 *
 */
public class OperationMetadata {
    
    private String subResourcePath;
    private HttpMethod httpMethod;
    private DataBinding inBinding;
    private DataBinding outBinding;
    private List<ParameterMetadata> parameters;
    
    public OperationMetadata(String subResourcePath, HttpMethod httpMethod, DataBinding inBinding, DataBinding outBinding, List<ParameterMetadata> parameters) {
        this.subResourcePath = subResourcePath;
        this.httpMethod = httpMethod;
        this.inBinding = inBinding;
        this.outBinding = outBinding;
        this.parameters = parameters;
    }

    public List<ParameterMetadata> getParameters() {
        return parameters;
    }

    public String getSubResourcePath() {
        return subResourcePath;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public DataBinding getInBinding() {
        return inBinding;
    }

    public DataBinding getOutBinding() {
        return outBinding;
    }

}
