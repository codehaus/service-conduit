package org.sca4j.bpel.lightweight.model;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:42:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImportDefinition {

    private String location;
    private String importType;
    private String namespace;

    public ImportDefinition(String location, String importType, String namespace) {
        this.location = location;
        this.importType = importType;
        this.namespace = namespace;
    }

    public String getLocation() {
        return location;
    }

    public String getImportType() {
        return importType;
    }

    public String getNamespace() {
        return namespace;
    }

}
