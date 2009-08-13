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
package org.sca4j.spi.domain;

import javax.xml.namespace.QName;

import org.sca4j.host.domain.DeploymentException;
import org.sca4j.scdl.Composite;

/**
 * Represents a domain.
 *
 * @version $Rev: 5271 $ $Date: 2008-08-25 22:46:23 +0100 (Mon, 25 Aug 2008) $
 */
public interface Domain {

    /**
     * Include a deployable composite in the domain.
     *
     * @param deployable the name of the deployable composite to include
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(QName deployable) throws DeploymentException;

    /**
     * Include a deployable composite in the domain.
     *
     * @param deployable    the name of the deployable composite to include
     * @param transactional if true, the deployment operation will be done transactionally. That is, changes to the logical model will only be applied
     *                      after componnets have been deployed to a runtime or runtimes.
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(QName deployable, boolean transactional) throws DeploymentException;

    /**
     * Include a Composite in the domain.
     *
     * @param composite the composite to include
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(Composite composite) throws DeploymentException;

    /**
     * Include a Composite in the domain.
     *
     * @param composite     the composite to include
     * @param transactional if true, the deployment operation will be done transactionally. That is, changes to the logical model will only be applied
     *                      after componnets have been deployed to a runtime or runtimes.
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(Composite composite, boolean transactional) throws DeploymentException;

    /**
     * Remove a deployable Composite from the domain.
     *
     * @param deployable the name of the deployable composite to remove
     * @throws org.sca4j.host.domain.DeploymentException
     *          if an error is encountered during removal
     */
    void remove(QName deployable) throws DeploymentException;

    /**
     * Remove a deployable Composite from the domain.
     *
     * @param deployable    the name of the deployable composite to remove
     * @param transactional if true, the deployment operation will be done transactionally. That is, changes to the logical model will only be applied
     *                      after componnets have been deployed to a runtime or runtimes.
     * @throws org.sca4j.host.domain.DeploymentException
     *          if an error is encountered during removal
     */
    void remove(QName deployable, boolean transactional) throws DeploymentException;

    /**
     * Remove a Composite from the domain.
     *
     * @param composite the composite to remove
     * @throws DeploymentException if an error is encountered during removal
     */
    void remove(Composite composite) throws DeploymentException;

    /**
     * Remove a Composite from the domain.
     *
     * @param composite     the composite to remove
     * @param transactional if true, the deployment operation will be done transactionally. That is, changes to the logical model will only be applied
     *                      after componnets have been undeployed to a runtime or runtimes.
     * @throws DeploymentException if an error is encountered during removal
     */
    void remove(Composite composite, boolean transactional) throws DeploymentException;

}
