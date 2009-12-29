package com.travelex.tgbp.rules.dynamic.execute;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRules;
import com.travelex.tgbp.rules.dynamic.execute.api.RoutingDecision;
import com.travelex.tgbp.store.domain.Instruction;

public class DynamicRuleExecutionITest extends TestCase {

    @Reference protected DynamicRules dynamicRules;

    public void testExecution() throws Exception {
        Instruction instruction = getInstruction();
        RoutingDecision routingDecision = dynamicRules.getRouting("pain.001.001.03", instruction);
        assertTrue(routingDecision.routeSpecified());
        assertEquals("EBA", routingDecision.getClearingMechanism());
    }

    private Instruction getInstruction() {
        String paymentData = "<CdtTrfTxInf xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><PmtId><InstrId>ABC/090928/CCT001/01</InstrId><EndToEndId>ABC/4562/2009-09-08</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"JPY\">10000000</InstdAmt></Amt><ChrgBr>SHAR</ChrgBr><CdtrAgt><FinInstnId><BIC>AAAAGB2L</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>DEF Electronics</Nm><PstlAdr><AdrLine>Corn Exchange 5th Floor</AdrLine><AdrLine>Mark Lane 55</AdrLine><AdrLine>EC3R7NE London</AdrLine><AdrLine>GB</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><Othr><Id>23683707994125</Id></Othr></Id></CdtrAcct><Purp><Cd>CINV</Cd></Purp><RmtInf><Strd><RfrdDocInf><Nb>4562</Nb><RltdDt>2009-09-08</RltdDt></RfrdDocInf></Strd></RmtInf></CdtTrfTxInf>";
        String paymentGroupData = "<PmtInf xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><PmtInfId>ABC/086</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><ReqdExctnDt>2009-09-29</ReqdExctnDt><Dbtr><Nm>ABC Corporation</Nm><PstlAdr><StrtNm>Times Square</StrtNm><BldgNb>7</BldgNb><PstCd>NY 10036</PstCd><TwnNm>New York</TwnNm><Ctry>US</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><Othr><Id>00125574999</Id></Othr></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BBBBUS33</BIC></FinInstnId></DbtrAgt></PmtInf>";

        Instruction result = new Instruction("23683707994125", null, null, null) {
            public Long getKey() {
                return 25L;
            }
        };
        result.setPaymentData(paymentData);
        result.setPaymentGroupData(paymentGroupData);
        return result;
    }

}
