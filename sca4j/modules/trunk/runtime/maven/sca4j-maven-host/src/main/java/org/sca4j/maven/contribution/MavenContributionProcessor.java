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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.maven.contribution;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.fabric.services.contribution.processor.AbstractContributionProcessor;
import org.sca4j.fabric.util.FileHelper;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contribution.Action;
import org.sca4j.spi.services.contribution.Contribution;

/**
 * Processes a Maven module directory.
 *
 * @version $Rev: 4877 $ $Date: 2008-06-22 11:44:45 +0100 (Sun, 22 Jun 2008) $
 */
@EagerInit
public class MavenContributionProcessor extends AbstractContributionProcessor {
    
    @Reference public ContentTypeResolver contentTypeResolver;

    protected URL getManifestUrl(Contribution contribution) throws MalformedURLException {
        return new URL(contribution.getLocation().toExternalForm() + "/classes/META-INF/sca-contribution.xml");
    }

    protected void iterateArtifacts(Contribution contribution, Action action) throws ContributionException {
        File root = FileHelper.toFile(contribution.getLocation());
        assert root.isDirectory();
        iterateArtifactsResursive(contribution, action, root);
    }

    private void iterateArtifactsResursive(Contribution contribution, Action action, File dir) throws ContributionException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                iterateArtifactsResursive(contribution, action, file);
            } else {
                try {
                    URL entryUrl = file.toURI().toURL();
                    String contentType = contentTypeResolver.getContentType(entryUrl);
                    action.process(contribution, contentType, entryUrl);
                } catch (MalformedURLException e) {
                    throw new ContributionException(e);
                } catch (ContentTypeResolutionException e) {
                    throw new ContributionException(e);
                }
            }
        }

    }

}
