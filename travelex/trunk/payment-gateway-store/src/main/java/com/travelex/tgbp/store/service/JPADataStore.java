package com.travelex.tgbp.store.service;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;
@SuppressWarnings("unchecked")
public class JPADataStore implements DataStore {

    @PersistenceContext(unitName="tgbp-store") protected EntityManager entityManager;
    
    /**
     * {@inheritDoc}
     */
    public Long store(PersistentEntity entity) {
        entityManager.persist(entity);
        return entity.getKey();
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentEntity> List<T> execute(Query query, Object... params) {
        javax.persistence.Query q = entityManager.createNamedQuery(query.getJpaName());
        if(params != null) {
            int idx = 1;
            for (Object param : params) {
                q.setParameter(idx, param);
            }
        }

        return q.getResultList();
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentEntity> T lookup(Class<? extends PersistentEntity> entityClass, Long pk) {
        return (T) entityManager.find(entityClass, pk);
    }

    /**
     * {@inheritDoc}
     */
	public List<Instruction> findInstructionByCurrency(Currency currency) {		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism) {		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateOutputInstructionId(Long inputInstructionId, Long outputInstructionId) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateOutputSubmissionId(Long outputSubmissionId, Collection<OutputInstruction> outputInstructions) {
		
	}

}
