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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

/*
 * Some Maven internal classes deep clone the MavenProject including the model so
 * it needs to be stubbed too.
 */
public class SCAModelStub extends Model {
	
	public SCAModelStub() {}

	public String getVersion() {
		return "0.0-TEST";
	}

	public String getModelVersion() {
		return "0.0-TEST";
	}

	public String getName() {
		return "Test Model";
	}

	public String getGroupId() {
		return "org.sca4j";
	}

	public String getPackaging() {
		return "sca-contribution";
	}

	public Parent getParent() {
		return new Parent();
	}

	public String getArtifactId() {
		return "sca-contribution-plugin-test";
	}

	public Properties getProperties() {
		return new Properties();
	}

	public List getPackages() {
		return new LinkedList();
	}

	public List getProfiles() {
		return new LinkedList();
	}

	public List getModules() {
		return new LinkedList();
	}
	
}
