package com.travelex.tgbp.rules.dynamic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.OutputInstruction;
import com.travelex.tgbp.store.domain.OutputSubmission;
import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;
import com.travelex.tgbp.store.type.ClearingMechanism;
import com.travelex.tgbp.store.type.Currency;

@SuppressWarnings("unchecked")
public class DummyDataStore implements DataStore {

    public Long store(PersistentEntity entity) {
        return entity.getKey();
    }

    public <T extends PersistentEntity> List<T> execute(Query query, Object... params)
    {
        List result = new ArrayList();
        InputStream ruleData = Thread.currentThread().getContextClassLoader().getResourceAsStream("input.xml");
        try {
            DynamicRule rule = new DynamicRule("EBA", "Route to EBA", IOUtils.toString(ruleData), "pain.001.001.03");
            result.add(rule);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public <T extends PersistentEntity> T lookup(Class<? extends PersistentEntity> arg0, Long arg1) {
        Submission s = new Submission(null, null);
        s.setMessageId("MSG123");
        String submissionHeader = "<CstmrCdtTrfInitn xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><GrpHdr><MsgId>ABC/090928/CCT001</MsgId><CreDtTm>2009-09-28T14:07:00</CreDtTm><NbOfTxs>3</NbOfTxs><CtrlSum>11500000</CtrlSum><InitgPty><Nm>ABC Corporation</Nm><PstlAdr><StrtNm>Times Square</StrtNm><BldgNb>7</BldgNb><PstCd>NY 10036</PstCd><TwnNm>New York</TwnNm><Ctry>US</Ctry></PstlAdr></InitgPty></GrpHdr></CstmrCdtTrfInitn>";
        s.setSubmissionHeader(submissionHeader);
        return (T)s;
    }

    public List<Instruction> findInstructionByCurrency(Currency currency) {throw new UnsupportedOperationException();}
    public List<OutputInstruction> findOutputInstructionByClearingMechanism(ClearingMechanism clearingMechanism, LocalDate maxValueDate) {throw new UnsupportedOperationException();}
    public void updateInstructionForOutput(Long inputInstructionId, Long outputInstructionId) {}
    public int getCount(Query query, Object... params) {throw new UnsupportedOperationException("Not Implemented");}
    public List<String> getInstructionTotals(LocalDate date) {throw new UnsupportedOperationException("Not Implemented");}

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
