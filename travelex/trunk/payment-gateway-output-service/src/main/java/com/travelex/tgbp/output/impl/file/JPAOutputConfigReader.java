package com.travelex.tgbp.output.impl.file;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.travelex.tgbp.output.service.file.OutputConfigReader;
import com.travelex.tgbp.output.types.OutputInstructionBatchingConfig;
import com.travelex.tgbp.output.types.RemittanceAccountConfig;
import com.travelex.tgbp.store.type.ClearingMechanism;

/**
 * JPA implementation for {@link OutputConfigReader}.
 */
public class JPAOutputConfigReader implements OutputConfigReader {

    @PersistenceContext(unitName = "tgbp-output") protected EntityManager entityManager;

    private static final String FIND_OI_BATCHING_CONFIG = "FIND_OI_BATCHING_CONFIG";
    private static final String FIND_REMITTANCE_ACCOUNT = "FIND_REMITTANCE_ACCOUNT";

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputInstructionBatchingConfig findBatchingConfigByClearingMechanism(ClearingMechanism clearingMechanism) {
        final Query query = entityManager.createNamedQuery(FIND_OI_BATCHING_CONFIG);
        query.setParameter(1, clearingMechanism);
        return (OutputInstructionBatchingConfig) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemittanceAccountConfig findRemittanceConfigByClearingMechanism(ClearingMechanism clearingMechanism) {
        final Query query = entityManager.createNamedQuery(FIND_REMITTANCE_ACCOUNT);
        query.setParameter(1, clearingMechanism);
        return (RemittanceAccountConfig) query.getSingleResult();
    }

}
