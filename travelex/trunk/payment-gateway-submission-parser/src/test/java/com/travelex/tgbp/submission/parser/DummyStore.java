package com.travelex.tgbp.submission.parser;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

public class DummyStore implements DataStore {
	
	@Reference protected Store store;
	
	@Override
	public Long store(PersistentEntity entity) {
		if (entity instanceof Submission){
			Long id = 25L;
            setPrivateField(entity, "id", id);
            store.addSubmission((Submission)entity);
            return id;
		} else if(entity instanceof Instruction) {
			store.addInstruction((Instruction)entity);
			return 3L;
		}else {
			return 1L;
		}
	}

    public <T extends PersistentEntity> List<T> execute(Query query, Object... params) {return null;}
	public List<Instruction> findInstructionByCurrency(Currency currency) {return null;}
	public List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism) {return null;}
	public <T extends PersistentEntity> T lookup(Class<? extends PersistentEntity> entityClass, Long id) {return null;}
	public void updateOutputInstructionId(Long inputInstructionId, Long outputInstructionId) {}
	public void updateOutputSubmissionId(Long outputSubmissionId, Collection<OutputInstruction> outputInstructions) {}
	
	
	 private void setPrivateField(Object object, String fieldName, Object value) {
	        try {
	            PrivateAccessor.setField(object, fieldName, value);
	        } catch (NoSuchFieldException e) {
	            TestCase.fail(e.getMessage());
	        }
	    }

}
