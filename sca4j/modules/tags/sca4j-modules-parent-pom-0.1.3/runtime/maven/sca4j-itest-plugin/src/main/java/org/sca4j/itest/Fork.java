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
