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
package org.sca4j.war;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

/**
 * Represents a configured SCA4J dependency for boot and extension libraries.
 *
 * @version $Rev: 5377 $ $Date: 2008-09-09 18:29:48 +0100 (Tue, 09 Sep 2008) $
 */
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String type = "jar";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Default constructor.
     */
    public Dependency() {
    }

    /**
     * Initializes the field.
     *
     * @param groupId    Group id.
     * @param artifactId Artifact id.
     * @param version    Artifact version.
     */
    public Dependency(String groupId, String artifactId, String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    /**
     * Gets the artifact using the specified artifact factory.
     *
     * @param artifactFactory Artifact factory to use.
     * @return Artifact identified by the dependency.
     */
    public Artifact getArtifact(ArtifactFactory artifactFactory) {
        return artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_RUNTIME, type);
    }

    /**
     * Checks whether the specified artifact has the same artifact id.
     *
     * @param artifact Artifact to be matched.
     * @return True if the specified artifact has the same id.
     */
    public boolean match(Artifact artifact) {
        return artifact.getArtifactId().equals(artifactId);
    }

    /**
     * Returns the version of dependency.
     *
     * @return the version of dependency
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the version for dependency.
     *
     * @param version the version for dependency
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the artifact id.
     *
     * @return the artifact id
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Returns the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dependency that = (Dependency) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
