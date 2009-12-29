package com.travelex.tgbp.store.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.joda.time.LocalDate;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.OutputSubmission;
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
        javax.persistence.Query q = populateQuery(query, params);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public int getCount(Query query, Object... params) {
        javax.persistence.Query q = populateQuery(query, params);
        Long res = (Long) q.getSingleResult();
        return res.intValue();
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
    public int update(Query query, Object... params) {
        return populateQuery(query, params).executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public List<Instruction> findInstructionByCurrency(Currency currency) {
        javax.persistence.Query jpaQuery = entityManager.createNamedQuery("FIND_INS_BY_CURR");
        jpaQuery.setParameter(1, currency);
        return jpaQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism) {
        javax.persistence.Query jpaQuery = entityManager.createNamedQuery("FIND_OUT_INS_BY_CLM");
        jpaQuery.setParameter(1, clearingMechanism);
        return jpaQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public void updateInstructionForOutput(Long inputInstructionId, Long outputInstructionId) {
        javax.persistence.Query jpaQuery = entityManager.createNamedQuery("UPDATE_INS");
        jpaQuery.setParameter(1, outputInstructionId);
        jpaQuery.setParameter(2, inputInstructionId);
        jpaQuery.executeUpdate();
    }

    @Override
    public List<String> getInstructionTotals(LocalDate date) {
        javax.persistence.Query query = populateQuery(Query.INSTRUCTION_TOTALS_BY_DATE, date);
        return query.getResultList();
    }

    @Override
    public List<String> getOutputInstructionTotals() {
        javax.persistence.Query query = populateQuery(Query.GET_OUTPUT_VALUES);
        return query.getResultList();
    }

    @Override
    public String getMostRecentRoute() {
        javax.persistence.Query q = entityManager.createNamedQuery(Query.GET_MOST_RECENT_ROUTE.getJpaName());
        ClearingMechanism  csm = (ClearingMechanism) q.getResultList().get(0);
        return csm.name();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getInstructionDataByMssgId(String messageId) {
        Session session = (Session)entityManager.getDelegate();
        List data = session.createSQLQuery("select i.currency curr, i.amount amount, i.value_date vdate, " +
                               "os.file_name fname from submission s, instruction i, " +
                               "output_instruction oi, output_submission os where s.message_id=? " +
                               "and i.sub_id=s.id and i.out_ins_id is not null and " +
                               "i.out_ins_id = oi.id and oi.out_sub_id = os.id").addScalar("curr").
                               addScalar("amount").addScalar("vdate").addScalar("fname").setString(0, messageId).list();
        return data.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public OutputSubmission getMostRecentOutputSubmission() {
        javax.persistence.Query q = entityManager.createNamedQuery(Query.GET_MOST_RECENT_OUTPUT_SUBMISSION.getJpaName());
        return (OutputSubmission) q.getResultList().get(0);
    }

    private javax.persistence.Query populateQuery(Query query, Object... params) {
        javax.persistence.Query q = entityManager.createNamedQuery(query.getJpaName());
        if(params != null) {
            int idx = 1;
            for (Object param : params) {
                q.setParameter(idx++, param);
            }
        }
        return q;
    }

}
