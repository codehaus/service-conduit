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
