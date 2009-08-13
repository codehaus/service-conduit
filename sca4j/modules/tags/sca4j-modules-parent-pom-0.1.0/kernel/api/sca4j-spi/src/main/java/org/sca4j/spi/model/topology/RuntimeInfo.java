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
package org.sca4j.spi.model.topology;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;

/**
 * Tracks information regarding a runtime service node, including available capabilities and resources
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class RuntimeInfo {

    public static final QName QNAME = new QName(Namespaces.SCA4J_NS, "runtimeInfo");

    public enum Status {
        STARTED, STOPPED
    }

    private URI id;
    private Set<URI> components = new HashSet<URI>();
    private Set<QName> features;
    private long uptime;
    private Status status;
    private String messageDestination;
    private Map<QName, String> transportInfo = new HashMap<QName, String>();

    public RuntimeInfo() {
        components = new HashSet<URI>();
    }

    public RuntimeInfo(URI id) {
        this();
        this.id = id;
    }

    /**
     * Returns the runtime status.
     *
     * @return Runtime status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the runtime status.
     *
     * @param status Runtime status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns the features available on the runtime.
     *
     * @return Features available on the runtime.
     */
    public Set<QName> getFeatures() {
        return features;
    }

    /**
     * Sets the features available on the runtime.
     *
     * @param features Features available on the runtime.
     */
    public void setFeatures(Set<QName> features) {
        this.features = features;
    }

    /**
     * Returns the uptime for the runtime.
     *
     * @return Uptime for the runtime.
     */
    public long getUptime() {
        return uptime;
    }

    /**
     * Sets the uptime for the runtime.
     *
     * @param uptime Uptime for the runtime.
     */
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    /**
     * Returns the runtime id.
     *
     * @return the runtime id
     */
    public URI getId() {
        return id;
    }

    /**
     * Returns the list of active components hosted by the runtime.
     *
     * @return the list of active components hosted by the runtime
     */
    public Set<URI> getComponents() {
        return components;
    }

    /**
     * Adds a a component name to the list of active components hosted by the runtime
     *
     * @param uri the component name
     */
    public void addComponent(URI uri) {
        components.add(uri);
    }

    /**
     * Returns the message destination used by this runtime.
     *
     * @return Message destination used by this runtime.
     */
    public String getMessageDestination() {
        return messageDestination;
    }

    /**
     * Sets the message destination used by this runtime.
     *
     * @param messageDestination Message destination used by this runtime.
     */
    public void setMessageDestination(String messageDestination) {
        this.messageDestination = messageDestination;
    }

    /**
     * Sets the opaque metadata for binding transports supported by the runtime. Transport information is keyed by a QName representing the transport
     * type (e.g. HTTP or HTTPs) and contains opaque metadata pertaining to the transport such as port number or queue name.
     *
     * @param info the transport information
     */
    public void setTransportInfo(Map<QName, String> info) {
        transportInfo.putAll(info);
    }

    /**
     * Returns the opaque metadata for a transport supported by the runtime.
     *
     * @param transport the metadata or null if the transport is not supported
     * @return the QName representing the transport
     */
    public String getTransportMetaData(QName transport) {
        return transportInfo.get(transport);
    }

    /**
     * Returns the opaque metadata for binding transports supported by the runtime. Transport information is keyed by a QName representing the
     * transport type (e.g. HTTP or HTTPs) and contains opaque metadata pertaining to the transport such as port number or queue name.
     *
     * @return the metadata for transports supported by the runtime
     */
    public Map<QName, String> getTransportInfo() {
        return transportInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == RuntimeInfo.class && obj.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
