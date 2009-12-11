package com.travelex.tgbp.transform.api.resource;

/**
 * A source for a transform that will be used to ensure source data is only parsed once.
 * 
 * @param <SRC> the source type
 * @param <PSRC> the parsed source type
 */
public class TransformSource<SRC, PSRC> {

    /** The source. */
    private final SRC source;

    /** The parsed source. */
    private PSRC parsedSource = null;

    /**
     * Default constructor.
     * 
     * @param source the source
     */
    public TransformSource(SRC source) {
        this.source = source;
    }

    /**
     * Return the original source.
     * 
     * @return the source
     */
    public SRC getSource() {
        return source;
    }

    /**
     * Set the parsed source.
     * 
     * @param parsedSource the parsed source
     */
    public void setParsedSource(PSRC parsedSource) {
        this.parsedSource = parsedSource;
    }

    /**
     * Get the parsed source.
     * 
     * @return the parsed source
     */
    public PSRC getParsedSource() {
        return parsedSource;
    }

    /**
     * Return true if this object contains parsed source.
     * 
     * @return true, if checks if is parsed
     */
    public boolean isParsed() {
        return parsedSource != null;
    }
}