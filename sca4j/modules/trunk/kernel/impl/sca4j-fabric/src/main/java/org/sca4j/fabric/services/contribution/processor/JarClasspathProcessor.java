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
package org.sca4j.fabric.services.contribution.processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.util.IOHelper;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.services.contribution.ClasspathProcessor;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;

/**
 * Creates a classpath based on the contents of a jar. Specifically, adds the jar and any zip/jar archives found in META-INF/lib to the classpath
 *
 * @version $Rev: 2450 $ $Date: 2008-01-10 22:09:41 +0000 (Thu, 10 Jan 2008) $
 */
@EagerInit
public class JarClasspathProcessor implements ClasspathProcessor {
    // system property to set when exploding jars
    private static final String EXTRACT = "fabric3.extensions.dependencies.extract";

    private final ClasspathProcessorRegistry registry;
    private HostInfo hostInfo;

    public JarClasspathProcessor(@Reference ClasspathProcessorRegistry registry, @Reference HostInfo hostInfo) {
        this.registry = registry;
        this.hostInfo = hostInfo;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    @Destroy
    public void destroy() {
        registry.unregister(this);
    }


    public boolean canProcess(URL url) {
        String name = url.getFile().toLowerCase();
        return name.endsWith(".jar") || name.endsWith(".zip");
    }

    public List<URL> process(URL url) throws IOException {
        List<URL> classpath = new ArrayList<URL>();
        // add the the jar itself to the classpath
        classpath.add(url);

        // add libraries from the jar
        addLibraries(classpath, url);
        return classpath;
    }

    private void addLibraries(List<URL> classpath, URL jar) throws IOException {

        File dir = hostInfo.getTempDir();

        InputStream is = jar.openStream();
        try {
            JarInputStream jarStream = new JarInputStream(is);
            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String path = entry.getName();
                if (!path.startsWith("META-INF/lib/")) {
                    continue;
                }
                String fileName = path.substring(path.lastIndexOf('/'));
                File explodedDirectory = new File(dir, fileName);
                explodeJar(dir, jarStream, explodedDirectory);
                classpath.add(explodedDirectory.toURI().toURL());
            }
        } finally {
            is.close();
        }
    }

    private void explodeJar(File dir, JarInputStream jarStream, File explodedDirectory) throws IOException, FileNotFoundException {

        if (!explodedDirectory.exists()) {

            explodedDirectory.mkdirs();
            File jarFile = File.createTempFile("sca4j", ".jar", dir);
            jarFile.createNewFile();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(jarFile));

            try {
                IOHelper.copy(jarStream, os);
                os.flush();
            } finally {
                os.close();
            }

            try {

                FileInputStream inputStream = new FileInputStream(jarFile);
                JarInputStream jarInputStream = new JarInputStream(inputStream);

                JarEntry entry;
                while ((entry = jarInputStream.getNextJarEntry()) != null) {

                    String filePath = entry.getName();
                    if (entry.isDirectory()) {
                        continue;
                    }

                    File entryFile = new File(explodedDirectory, filePath);
                    entryFile.getParentFile().mkdirs();

                    entryFile.createNewFile();
                    OutputStream entryOutputStream = new BufferedOutputStream(new FileOutputStream(entryFile));
                    IOHelper.copy(jarInputStream, entryOutputStream);
                    entryOutputStream.flush();
                    entryOutputStream.close();

                }

                inputStream.close();

            } finally {
                jarFile.delete();
            }
            
        }
        
    }
    
}

