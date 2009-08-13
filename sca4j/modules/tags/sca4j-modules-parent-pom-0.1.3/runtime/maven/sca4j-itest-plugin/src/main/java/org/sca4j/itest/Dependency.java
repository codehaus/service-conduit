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

import java.util.List;

/**
 * Local subclass of Maven's dependency as Plexus looks for elements in the same package.
 *
 * @version $Rev: 5090 $ $Date: 2008-07-26 01:23:06 +0100 (Sat, 26 Jul 2008) $
 */
public class Dependency extends org.apache.maven.model.Dependency {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2603000897594439278L;
    
    /**
     * No-argument constructor.
     */
    public Dependency() {
    }
    
    /**
     * Sets the group id, artifact id, version and exclusions.
     * 
     * @param groupId Group Id.
     * @param artifactId ARtifact Id.
     * @param version Version.
     * @param exclusions List of exclusions.
     */
    public Dependency(String groupId, String artifactId, String version, List<Exclusion> exclusions) {
        setGroupId(groupId);
        setVersion(version);
        setArtifactId(artifactId);
        setExclusions(exclusions);
    }

    /**
     * Implements equals based onartifactId, groupId and version.
     */
    @Override
    public boolean equals(Object obj) {
        
        if (!(obj instanceof Dependency)) {
            return false;
        }
        
        Dependency other = (Dependency) obj;
        return getArtifactId().equals(other.getArtifactId()) && 
               getGroupId().equalsIgnoreCase(other.getGroupId()) && 
               getVersion().equals(other.getVersion());
        
    }

    /**
     * Implements hashCode based onartifactId, groupId and version.
     */
    @Override
    public int hashCode() {
        
        int hash = 7;
        hash += 31 * getArtifactId().hashCode();
        hash += 31 * getGroupId().hashCode();
        hash += 31 * getVersion().hashCode();
        
        return hash;
        
    }
    
}
