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

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;

/*
 * Represents a maven artifact that is referenced or generated during the build
 */
public class SCAArtifactStub extends ArtifactStub {

	public SCAArtifactStub() {
		super();
	}

	public String getVersion() {
		if (super.getVersion() == null) {
			super.setVersion("0.0-Test");
		}
		return super.getVersion();
	}

	public String getArtifactId() {
		if (super.getArtifactId() == null) {
			super.setArtifactId("sca-contribution-plugin-test");
		}
		return super.getArtifactId();
	}

	public String getGroupId() {
		if (super.getGroupId() == null) {
			super.setGroupId("org.sca4j");
		}
		return super.getGroupId();
	}

	public String getClassifier() {
		return super.getClassifier();
	}

	public String getScope() {
		if (super.getScope() == null) {
			super.setScope(Artifact.SCOPE_RUNTIME);
		}
		return super.getScope();
	}

	public boolean isOptional() {
		return super.isOptional();
	}

	public String getType() {
		if (super.getType() == null) {
			super.setType("sca-contribution");
		}
		return super.getType();
	}

	public ArtifactHandler getArtifactHandler() {
		return new DefaultArtifactHandler(getType());
	}

	public VersionRange getVersionRange() {
		return VersionRange.createFromVersion(getVersion());
	}
}
