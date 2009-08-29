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
package org.sca4j.itest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import com.thoughtworks.xstream.XStream;

public class Fork {
    
    public void run(TestMetadata testMetadata, Log log, String jvmargs) throws IOException, MojoExecutionException, InterruptedException {
        
        UUID uuid = UUID.randomUUID();
        File file = new File(uuid.toString());
        file.createNewFile();
        file.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        
        XStream xstream = new XStream();
        xstream.toXML(testMetadata, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        
        StringBuilder command = buildCommand(jvmargs, file);
        
        log.info(command);
        
        Process process = Runtime.getRuntime().exec(command.toString());
        
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new ProcessDrainer(process.getInputStream(), log));
        executorService.submit(new ProcessDrainer(process.getErrorStream(), log));
        
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new MojoExecutionException("For failed, error code: " + exitCode);
        }
        
    }

    private StringBuilder buildCommand(String jvmargs, File file) {
        
        StringBuilder command = new StringBuilder("java -cp ");
        
        URL[] urls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
        for (int i = 0; i < urls.length; i++) {
            command.append(urls[i]);
            if (i != urls.length - 1) {
                command.append(File.pathSeparator);
            }
        }
        command.append(command.toString());
        
        if (jvmargs != null) {
            command.append(" " + jvmargs);
        }
        
        command.append(" " + TestRunner.class.getName());
        command.append(" " + file.getAbsolutePath());
        
        return command;
        
    }
    
    private class ProcessDrainer implements Runnable {
        
        private InputStream inputStream;
        private Log log;
        
        private ProcessDrainer(InputStream inputStream, Log log) {
            this.inputStream = inputStream;
            this.log = log;
        }
        
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                while (line != null) {
                    log.info(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                log.error(e);
                throw new AssertionError(e);
            }
        }
        
    }

}
