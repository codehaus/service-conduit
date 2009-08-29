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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.spi.util;

import java.net.URI;

/**
 * Utility methods for handling URIs
 *
 * @version $Rev: 2296 $ $Date: 2007-12-22 09:48:39 +0000 (Sat, 22 Dec 2007) $
 */
public final class UriHelper {

    private UriHelper() {
    }

    /**
     * Returns the base name for a component URI, e.g. 'Bar' for 'sca://foo/Bar'
     *
     * @param uri the URI to parse
     * @return the base name
     */
    public static String getBaseName(URI uri) {
        String s = uri.toString();
        int pos = s.lastIndexOf('/');
        if (pos > -1) {
            return s.substring(pos + 1);
        } else {
            return s;
        }
    }

    public static String getParentName(URI uri) {
        String s = uri.toString();
        int pos = s.lastIndexOf('/');
        if (pos > -1) {
            return s.substring(0, pos);
        } else {
            return null;
        }
    }


    public static URI getDefragmentedName(URI uri) {
        if (uri.getFragment() == null) {
            return uri;
        }
        return URI.create(getDefragmentedNameAsString(uri));
    }

    public static String getDefragmentedNameAsString(URI uri) {
        if (uri.getFragment() == null) {
            return uri.toString();
        }
        String s = uri.toString();
        int pos = s.lastIndexOf('#');
        return s.substring(0, pos);
    }

}
