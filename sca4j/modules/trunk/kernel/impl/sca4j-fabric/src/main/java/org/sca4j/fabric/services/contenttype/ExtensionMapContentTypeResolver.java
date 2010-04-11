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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.fabric.services.contenttype;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.activation.FileTypeMap;
import org.oasisopen.sca.annotation.Property;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;

/**
 * Content type resolver that is implemented using a configured map.
 *
 * @version $Revision: 5235 $ $Date: 2008-08-20 19:42:12 +0100 (Wed, 20 Aug 2008) $
 */
public class ExtensionMapContentTypeResolver implements ContentTypeResolver {

    // Unknown content
    private static final String UNKNOWN_CONTENT = "content/unknown";

    // Extension to content type map
    private Map<String, String> extensionMap = new HashMap<String, String>();

    private FileTypeMap typeMap = FileTypeMap.getDefaultFileTypeMap();

    /**
     * @param extensionMap Injected extension map.
     */
    @Property(required=false)
    public void setExtensionMap(Map<String, String> extensionMap) {
        this.extensionMap = extensionMap;
    }

    public String getContentType(URL contentUrl) throws ContentTypeResolutionException {

        if (contentUrl == null) {
            throw new IllegalArgumentException("Content URL cannot be null");
        }

        String urlString = contentUrl.toExternalForm();

        try {

            URLConnection connection = contentUrl.openConnection();
            String extension = urlString.substring(urlString.lastIndexOf('.') + 1);
            String contentType = extensionMap.get(extension);
            if (contentType != null) {
                return contentType;
            }
            contentType = connection.getContentType();

            if (contentType == null || UNKNOWN_CONTENT.equals(contentType) || "application/octet-stream".equals(contentType)) {
                String filename = contentUrl.getFile();
                contentType = typeMap.getContentType(filename);
            }

            return contentType;
        } catch (IOException ex) {
            throw new ContentTypeResolutionException("Unable to resolve content type: " + urlString, urlString, ex);
        }

    }

}
