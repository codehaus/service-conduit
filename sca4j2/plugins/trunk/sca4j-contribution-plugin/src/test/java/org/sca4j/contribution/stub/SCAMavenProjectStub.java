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

package org.sca4j.contribution.stub;

import java.util.HashSet;
import java.util.LinkedList;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
/*
 * This is a stub class for the maven project. Attempting to extend MavenProjectStub didn't
 * work. A real POM could be read in during each test but since we are testing only
 * the mojo it doesn't make sense. This class fills in the stub so the clone operations succeed.
 */
public class SCAMavenProjectStub extends MavenProject{
	
	public SCAMavenProjectStub(Model model){
		super(model);
        super.setDependencyArtifacts( new HashSet() );
        super.setArtifacts( new HashSet() );
        super.setPluginArtifacts( new HashSet() );
        super.setReportArtifacts( new HashSet() );
        super.setExtensionArtifacts( new HashSet() );
        super.setRemoteArtifactRepositories( new LinkedList() );
        super.setPluginArtifactRepositories( new LinkedList() );
        super.setCollectedProjects( new LinkedList() );
        super.setActiveProfiles( new LinkedList() );
        //super.setOriginalModel( model );
        super.setOriginalModel( null );
        super.setExecutionProject( this );
	}

}
