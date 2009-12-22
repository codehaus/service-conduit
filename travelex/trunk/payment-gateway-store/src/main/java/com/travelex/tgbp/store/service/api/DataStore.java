package com.travelex.tgbp.store.service.api;

import java.util.Collection;
import java.util.List;

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
    void updateOutputInstructionId(Long inputInstructionId, Long outputInstructionId);

    /**
     * Updates output instructions with output submission id.
     *
     * @param outputSubmissionId - id of output submission to which this output instruction belong to.
     * @param outputInstructions - collection of output instructions to be updated.
     */
    void updateOutputSubmissionId(Long outputSubmissionId, Collection<OutputInstruction> outputInstructions);

}
