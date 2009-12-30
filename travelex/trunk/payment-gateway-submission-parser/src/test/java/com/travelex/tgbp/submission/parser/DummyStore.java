package com.travelex.tgbp.submission.parser;

import java.util.List;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.joda.time.LocalDate;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.OutputSubmission;
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

    public <T extends PersistentEntity> List<T> execute(Query query, Object... params) {throw new UnsupportedOperationException("Not Implemented");}
    public List<Instruction> findInstructionByCurrency(Currency currency) {throw new UnsupportedOperationException("Not Implemented");}
    public List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism, LocalDate maxValueDate) {throw new UnsupportedOperationException("Not Implemented");}
    public <T extends PersistentEntity> T lookup(Class<? extends PersistentEntity> entityClass, Long id) {throw new UnsupportedOperationException("Not Implemented");}
    public void updateInstructionForOutput(Long inputInstructionId, Long outputInstructionId) {throw new UnsupportedOperationException("Not Implemented");}
    public int getCount(Query query, Object... params) {throw new UnsupportedOperationException("Not Implemented");}
    public List<String> getInstructionTotals(LocalDate date) {throw new UnsupportedOperationException("Not Implemented");}

    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            PrivateAccessor.setField(object, fieldName, value);
        } catch (NoSuchFieldException e) {
            TestCase.fail(e.getMessage());
        }
    }

    @Override
    public Object[] getInstructionDataByMssgId(String mssagId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMostRecentRoute() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getOutputInstructionTotals() {
        // TODO Auto-generated method stub
        return null;
    }

    public int update(Query query, Object... params) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public OutputSubmission getMostRecentOutputSubmission(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateInstructionStatusForOutput(Long outputInstructionId) {
        // TODO Auto-generated method stub
    }

}
