package com.travelex.tgbp.rules.dynamic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

@SuppressWarnings("unchecked")
public class DummyDataStore implements DataStore {

    public <T> T store(PersistentEntity<T> entity) {
        return entity.getKey();
    }

    public <T extends PersistentEntity<?>> List<T> execute(Query query, Object... params)
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

    @Override
    public <T extends PersistentEntity<?>> T lookup(Class<? extends PersistentEntity<?>> arg0, Object arg1) {
        Submission s = new Submission("MSG123");
        String submissionHeader = "<CstmrCdtTrfInitn xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><GrpHdr><MsgId>ABC/090928/CCT001</MsgId><CreDtTm>2009-09-28T14:07:00</CreDtTm><NbOfTxs>3</NbOfTxs><CtrlSum>11500000</CtrlSum><InitgPty><Nm>ABC Corporation</Nm><PstlAdr><StrtNm>Times Square</StrtNm><BldgNb>7</BldgNb><PstCd>NY 10036</PstCd><TwnNm>New York</TwnNm><Ctry>US</Ctry></PstlAdr></InitgPty></GrpHdr></CstmrCdtTrfInitn>";
        s.setSubmissionHeader(submissionHeader);
        return (T)s;
    }
}
