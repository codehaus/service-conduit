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
