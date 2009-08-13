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
package org.sca4j.fabric.services.contenttype;

import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;

import javax.activation.FileTypeMap;
import org.osoa.sca.annotations.Property;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;

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
    @Property
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
