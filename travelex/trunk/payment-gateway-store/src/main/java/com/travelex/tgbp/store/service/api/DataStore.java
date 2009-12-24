package com.travelex.tgbp.store.service.api;

import java.util.List;

import org.joda.time.LocalDate;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

/**
 * Generic Data Store Interface used across most TX entities
 */
public interface DataStore {

    /**
     * Store the Persistent Entity
     * @param entity
     * @return Long of stored entity
     */
    Long store(PersistentEntity entity);

    /**
     * Find by id
     * @param <T>
     * @param entityClass
     * @param pk
     * @return {@link PersistentEntity}
     */
    <T extends PersistentEntity> T lookup(Class<? extends PersistentEntity> entityClass, Long id);

    /**
     * Find by given query and set of params
     * @param <T>
     * @param query
     * @param params
     * @return {@link PersistentEntity}
     */
    <T extends PersistentEntity> List<T> execute(Query query, Object... params);

    /**
     * Get the count which is the result of the subject query
     * @param query
     * @param params
     * @return the count
     */
    int getCount(Query query, Object... params);

    /**
     * Gives back input instructions on given currency for which no output instruction has been created.
     *
     * @param currency - instruction currency.
     * @return list of instructions.
     */
    List<Instruction> findInstructionByCurrency(Currency currency);

    /**
     * Gives back output instructions on given clearing mechanism which doesn't belong to any output submission.
     *
     * @param clearingMechanism - search criteria
     * @return list of output instructions.
     */
    List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism);

    /**
     * Updates input instruction with associated output instruction id.
     *
     * @param inputInstructionId - id of input instruction to update.
     * @param outputInstructionId - id of corresponding output instruction.
     */
    void updateInstructionForOutput(Long inputInstructionId, Long outputInstructionId);

    /**
     * Get the total instruction value, grouped by currency, for a given day.
     *
     * @param date - search criteria
     * @return list of Strings e.g. EUR/1000, USD/23.32
     */
    List<String> getInstructionTotals(LocalDate date);


    List<String> getOutputInstructionTotals();

    String getMostRecentRoute();

}
