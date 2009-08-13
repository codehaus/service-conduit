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

package org.sca4j.jetty.plugin.impl;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Clean up Mojo for SCA4J Jetty Plugin
 * @goal clean
 * @phase test-compile
 * @description Runs jetty6 directly from a maven project
 */
public class SCA4JJettyCleanMojo extends AbstractMojo {

	/**
	 * SCA4J path.
	 */
	private static final String FABRIC3_PATH = "WEB-INF/lib";

    /**
     * Root directory for all html/jsp etc files
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File webAppSourceDirectory;

	/*
	 * @see org.mortbay.jetty.plugin.Jetty6RunMojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		File bootDir = new File(webAppSourceDirectory, FABRIC3_PATH);
		deleteFile(bootDir);
	}

	/**
	 * Deletes the file created in sca4j-jetty:run
	 *
	 * @param file
	 */
	private void deleteFile(File file) {
		if(file.isDirectory()){
			for(File internalFile : file.listFiles()){
				deleteFile(internalFile);
			}
		}
		file.delete();
	}

}
