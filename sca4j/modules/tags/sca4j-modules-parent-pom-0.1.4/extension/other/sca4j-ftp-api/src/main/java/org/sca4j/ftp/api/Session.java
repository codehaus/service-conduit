package org.sca4j.ftp.api;

import java.util.List;

/**
 * Interface for an FTP session.
 * 
 * @author meerajk
 *
 */
public interface Session {

    /**
     * Gets all the quote commands.
     * 
     * @return Gets all the quote commands.
     */
    @SuppressWarnings("unchecked")
    public abstract List<String> getSiteCommands();

    /**
     * Gets the name of the user associated with the session.
     *
     * @return Name of the user associated with the session.
     */
    public abstract String getUserName();

    /**
     * Checks the whether the user is authenticated.
     *
     * @return True if the user is authenticated.
     */
    public abstract boolean isAuthenticated();

    /**
     * Returs the current working directory.
     *
     * @return the working directory.
     */
    public abstract String getCurrentDirectory();

    /**
     * Returns the data content type
     *
     * @return the file transfer type
     */
    public abstract String getContentType();

}