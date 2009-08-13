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
package org.sca4j.spi.services.contribution;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * A registry of classpath processors
 *
 * @version $Rev: 1052 $ $Date: 2007-09-05 18:16:29 +0100 (Wed, 05 Sep 2007) $
 */
public interface ClasspathProcessorRegistry {

    /**
     * Registers the processor
     *
     * @param processor the processor
     */
    void register(ClasspathProcessor processor);

    /**
     * De-registers the processor
     *
     * @param processor the processor
     */
    void unregister(ClasspathProcessor processor);

    /**
     * Processes the given url
     *
     * @param url the url to process
     * @return the classpath
     * @throws IOException if an error occurs processing the url
     */
    List<URL> process(URL url) throws IOException;
}
