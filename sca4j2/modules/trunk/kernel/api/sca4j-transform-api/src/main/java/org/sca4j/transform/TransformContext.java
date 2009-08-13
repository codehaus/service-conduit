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
package org.sca4j.transform;

import java.net.URL;

/**
 * Context information applicable during a transformation.
 *
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class TransformContext {
    private final ClassLoader targetClassLoader;
    private final ClassLoader sourceClassLoader;
    private final URL sourceBase;
    private final URL targetBase;

    /**
     * @param sourceClassLoader a ClassLoader that can be used to access resources from the source
     * @param targetClassLoader a ClassLoader that can be used instantiate transformation results
     * @param sourceBase        a URL for resolving locations from the source
     * @param targetBase        a URL for resolving locations for the target
     */
    public TransformContext(ClassLoader sourceClassLoader, ClassLoader targetClassLoader, URL sourceBase, URL targetBase) {
        this.sourceClassLoader = sourceClassLoader;
        this.targetClassLoader = targetClassLoader;
        this.sourceBase = sourceBase;
        this.targetBase = targetBase;
    }

    /**
     * Returns a ClassLoader that can be used instantiate transformation results.
     *
     * @return a ClassLoader that can be used instantiate transformation results
     */
    public ClassLoader getTargetClassLoader() {
        return targetClassLoader;
    }

    /**
     * Returns a ClassLoader that can be used to access resources from the source.
     *
     * @return a ClassLoader that can be used to access resources from the source
     */
    public ClassLoader getSourceClassLoader() {
        return sourceClassLoader;
    }

    /**
     * Returns a URL for resolving locations from the source.
     *
     * @return a URL for resolving locations from the source
     */
    public URL getSourceBase() {
        return sourceBase;
    }

    /**
     * Returns a URL for resolving locations for the target.
     *
     * @return a URL for resolving locations for the target
     */
    public URL getTargetBase() {
        return targetBase;
    }
}
