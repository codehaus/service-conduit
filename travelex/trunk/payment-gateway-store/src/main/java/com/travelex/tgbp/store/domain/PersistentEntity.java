package com.travelex.tgbp.store.domain;

/**
 * Base Entity
 */
public abstract class PersistentEntity {
	
	protected Long id;
		
	/**
	 * Return the id key
	 * @return id
	 */
    public Long getKey(){
    	return id;
    }

}
