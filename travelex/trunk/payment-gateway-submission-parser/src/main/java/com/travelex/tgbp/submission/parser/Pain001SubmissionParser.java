package com.travelex.tgbp.submission.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.joda.time.LocalDate;
import org.osoa.sca.annotations.Callback;
import org.sca4j.api.annotation.scope.Conversation;

import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.type.Currency;
import com.travelex.tgbp.submission.parser.api.SubmissionParser;
import com.travelex.tgbp.submission.parser.api.SubmissionParserListener;
import com.travelex.tgbp.submission.parser.api.SubmissionParsingException;

@Conversation
public class Pain001SubmissionParser implements SubmissionParser {

    @Callback protected SubmissionParserListener parserListener;

    private final Namespace NS = Namespace.getNamespace("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03");

    //For PoC purposes, this is the simplest possible implementation.
    public void parse(InputStream submissionData) {
        try {
            Document submission = new SAXBuilder().build(submissionData);
            Element transferInitiation = getChild(submission.getRootElement(), "CstmrCdtTrfInitn");
            parseSubmissionHeader(transferInitiation);
            createInstructions(transferInitiation);
        } catch (JDOMException e) {
            throw new SubmissionParsingException("Failed to process the submission document", e);
        } catch (IOException e) {
            throw new SubmissionParsingException("Failed to read the submission document", e);
        }

    }

    @Override
    public void close() {

    }

    private void createInstructions(Element transferInitiation) throws JDOMException, IOException {
        List<Element> paymentInfoBlocks = getChildren(transferInitiation, "PmtInf");
        System.out.println(paymentInfoBlocks.size());
        for (Element paymentInfo : paymentInfoBlocks) {
            LocalDate valueDate = new LocalDate(getChildText(paymentInfo, "ReqdExctnDt"));
            List<Element> instructions = getChildren(paymentInfo, "CdtTrfTxInf");
            List<Instruction> createdInstructions = new ArrayList<Instruction>();
            for (Element i : instructions) {
                Amount amount = getAmount(i);
                Instruction instruction = new Instruction(amount.currency, valueDate, amount.value);
                instruction.setPaymentData(asText(i));
                createdInstructions.add(instruction);
            }

            //This is the easiest way to get the group info data (without payment instructions)
            //and add it to the instruction. It can't be done this simply before processing all
            //instructions because the instructions list is live and would be affected by the remove.
            removeChildren(paymentInfo, "CdtTrfTxInf");
            String groupText = asText(paymentInfo);
            for (Instruction instruction : createdInstructions) {
                instruction.setPaymentGroupData(groupText);
                parserListener.onInstruction(instruction);
            }
        }

    }

    private String asText(Element e) throws IOException {
        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outputter.output(e, baos);
        return new String(baos.toByteArray());
    }

    private Amount getAmount(Element i) throws JDOMException {
        Element amt = (Element) createXPath("Amt/InstdAmt").selectSingleNode(i);
        if(amt == null) {
            amt = (Element) createXPath("Amt/EqvtAmt/Amt").selectSingleNode(i);
        }

        String currency = amt.getAttributeValue("Ccy");
        BigDecimal value = new BigDecimal(amt.getTextTrim());

        return new Amount(currency, value);
    }

    private class Amount {
        Currency currency;
        BigDecimal value;

        public Amount(String currency, BigDecimal value) {
            this.currency = Enum.valueOf(Currency.class, currency);
            this.value = value;
        }
    }

    private XPath createXPath(String baseExpression) throws JDOMException {
        XPath xPath = XPath.newInstance("p1:" + baseExpression.replaceAll("/", "/p1:"));
        xPath.addNamespace("p1", NS.getURI());
        return xPath;
    }

    private void parseSubmissionHeader(Element transferInitiation) throws IOException {
        //Remove payment info blocks to get the header data (without children) as text.
        List<Element> paymentInfoBlocks = getChildren(transferInitiation, "PmtInf");
        List<Content> detachedPaymentInfoBlocks = new ArrayList<Content>();
        for(int x = 0; x < paymentInfoBlocks.size(); x++) {
            detachedPaymentInfoBlocks.add(paymentInfoBlocks.get(x).detach());
        }
        String submissionHeader = asText(transferInitiation);

        //Re-attach so that subsequent processing is unaffected
        for(int x = 0; x < detachedPaymentInfoBlocks.size(); x++) {
            transferInitiation.addContent(detachedPaymentInfoBlocks.get(x));
        }

        Element groupHeader = getChild(transferInitiation, "GrpHdr");
        String messageId = getChildText(groupHeader, "MsgId");
        parserListener.onSubmissionHeader(messageId, submissionHeader);
    }

    private Element getChild(Element e, String localName) {
        return e.getChild(localName, NS);
    }

    private String getChildText(Element e, String localName) {
        return e.getChildText(localName, NS);
    }

    @SuppressWarnings("unchecked")
    private List<Element> getChildren(Element e, String localName) {
        return e.getChildren(localName, NS);
    }

    private void removeChildren(Element e, String localName) {
        e.removeChildren(localName, NS);

    }

}
