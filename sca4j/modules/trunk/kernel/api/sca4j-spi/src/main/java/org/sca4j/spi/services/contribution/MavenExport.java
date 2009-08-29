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
package org.sca4j.spi.services.contribution;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;

/**
 * Represents an Maven export entry in a contribution manifest.
 *
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public class MavenExport implements Export {
    private static final long serialVersionUID = 2622386855322390297L;
    private static final QName TYPE = new QName(Namespaces.SCA4J_NS, "maven");
    private String groupId;
    private String artifactId;
    private String version = "unspecified";
    private String classifier = "jar";
    private String majorVersion = "";
    private String minorVersion = "";
    private String revision = "";
    private boolean snapshot;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        parseVersion();
    }

    public String getMajorVersion() {
        return majorVersion;
    }

    public String getMinorVersion() {
        return minorVersion;
    }

    public String getRevision() {
        return revision;
    }

    public boolean isSnapshotVersion() {
        return snapshot;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public int match(Import contributionImport) {
        assert artifactId != null;
        assert groupId != null;
        if (contributionImport instanceof MavenImport) {
            MavenImport imprt = (MavenImport) contributionImport;
            assert imprt.getArtifactId() != null;
            assert imprt.getGroupId() != null;
            if (imprt.getArtifactId().equals(artifactId)
                    && imprt.getGroupId().equals(groupId)) {
                if (matchVersion(imprt)) {
                    return EXACT_MATCH;
                }
            }
        }
        return NO_MATCH;
    }

    public QName getType() {
        return TYPE;
    }

    private boolean matchVersion(MavenImport imprt) {
        if ("unspecified".equals(imprt.getVersion())) {
            return true;
        } else {
            if (imprt.getMajorVersion().compareTo(majorVersion) > 0) {
                return false;
            } else if (imprt.getMajorVersion().compareTo(majorVersion) == 0) {
                if (!"".equals(imprt.getMinorVersion()) && imprt.getMinorVersion().compareTo(minorVersion) > 0) {
                    return false;
                } else if (imprt.getMinorVersion().equals(minorVersion)) {
                    if (!"".equals(imprt.getRevision()) && imprt.getRevision().compareTo(revision) > 0) {
                        return false;
                    } else if (imprt.getRevision().equals(revision) && !imprt.isSnapshotVersion() && snapshot) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    private void parseVersion() {
        majorVersion = "";
        minorVersion = "";
        revision = "";
        snapshot = false;
        String[] tokens = version.split("\\.|\\-");
        if (tokens.length < 2) {
            majorVersion = version;
        } else if (tokens.length == 2) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
        } else if (tokens.length == 3) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
            if (!tokens[2].equals("SNAPSHOT")) {
                revision = tokens[2];
            } else {
                snapshot = true;
            }
        } else if (tokens.length == 4) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
            revision = tokens[2];
            snapshot = true;
        } else {
            throw new IllegalArgumentException("Illegal Maven version number: " + version);
        }
    }

    public String toString() {
        return new StringBuilder().append("Maven export [").append(groupId).append(":").append(artifactId).
                append(":").append(version).append(":").append(classifier).append("]").toString();
    }
}
