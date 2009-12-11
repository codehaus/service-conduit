package com.travelex.tgbp.transform.api;


/**
 * Service interface describing a generic transformation.
 * 
 * @param <S> the source type
 * @param <T> the transformed type
 * @param <CONTEXT> a type capable of delivering context information to the transformation process
 */

public interface TransformationService<S, T, CONTEXT> {

    /**
     * Transform an array of sources.
     * 
     * @param context the context information required for transformation (this usually corresponds with 'enrichment' data)
     * @param source an array of source data
     * 
     * @return a transformed object of type T
     */
    T transform(CONTEXT context, S... source);

}
