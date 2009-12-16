package com.travelex.tgbp.output.impl.file;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.travelex.tgbp.output.service.file.BatchingConfigReaderService;
import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * JPA implementation for {@link BatchingConfigReaderService}.
 */
public class JPABatchingConfigReaderService implements BatchingConfigReaderService {

    @PersistenceContext(unitName = "tgbp-output") protected EntityManager entityManager;

    private static final String FIND_OI_BATCHING_CONFIG = "FIND_OI_BATCHING_CONFIG";

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputInstructionBatchingConfig findByClearingMechanism(ClearingMechanism clearingMechanism) {
        final Query query = entityManager.createNamedQuery(FIND_OI_BATCHING_CONFIG);
        query.setParameter(1, clearingMechanism);
        return (OutputInstructionBatchingConfig) query.getSingleResult();
    }

}
