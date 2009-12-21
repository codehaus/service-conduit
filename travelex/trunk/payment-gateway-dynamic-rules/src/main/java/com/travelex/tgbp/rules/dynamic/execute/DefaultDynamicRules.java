package com.travelex.tgbp.rules.dynamic.execute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRuleException;
import com.travelex.tgbp.rules.dynamic.execute.api.DynamicRules;
import com.travelex.tgbp.rules.dynamic.execute.api.RoutingDecision;
import com.travelex.tgbp.store.domain.Instruction;
import com.travelex.tgbp.store.domain.Submission;
import com.travelex.tgbp.store.domain.rule.DynamicRule;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class DefaultDynamicRules implements DynamicRules {

    @Reference protected DataStore dataStore;

    //Have to also store namespaces of known schema names and look them up. This is OK for PoC.
    private static final Namespace NS = Namespace.getNamespace("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03");

    @Override
    public RoutingDecision getRouting(String schemaName, Instruction instruction) {
        RoutingDecision result = new RoutingDecision(false);
        try {
            Document instructionData = buildInstructionData(instruction);
            List<DynamicRule> dRules = dataStore.execute(Query.DYNAMIC_RULE_LOOKUP, schemaName);
            for(DynamicRule dRule: dRules) {
                List<Expression> expressions = ExpressionBuilder.build(dRule.getRuleText());
                boolean passed = expressions.size() > 0; //Should always be true but stops rules with no expressions passing.
                for (Expression expr : expressions) {
                    if(!passed) {
                        break; //Implicit ANDing of rules.
                    }
                    String actualData = getActualData(expr.getXPath(), instructionData.getRootElement());
                    passed = actualData != null &&
                    RuleExecutor.passes(actualData, expr.getRuleData(), expr.getOperator(), expr.getJavaType());
                }

                if(passed) {
                    result = new RoutingDecision(true, dRule.getClearingMechanism(), dRule.getRuleName());
                    break;
                }
            }
        } catch (JDOMException e) {
            throw new DynamicRuleException("Failed to parse rule expressions", e);
        } catch (IOException e) {
            throw new DynamicRuleException("Failed to parse rule expressions", e);
        }
        return result;
    }


    private String getActualData(String xPathExpression, Element srcDocRoot) throws JDOMException {
        String result = null;
        Element matched = (Element) createXPath(xPathExpression).selectSingleNode(srcDocRoot);
        if(matched != null) {
            result = matched.getText();
        }
        return result;
    }


    private Document buildInstructionData(Instruction instruction) throws JDOMException, IOException {
        Document iData = buildDocument(instruction.getPaymentData());
        Element iDataRoot = iData.detachRootElement();

        Document iGrpData = buildDocument(instruction.getPaymentGroupData());
        Element iGrpRoot = iGrpData.detachRootElement();

        Submission s = dataStore.lookup(Submission.class, instruction.getSubmissionId());
        Document sGrpData = buildDocument(s.getSubmissionHeader());
        Element sGrpRoot = sGrpData.detachRootElement();

        Document result = new Document();
        Element root = new Element("Document", NS);
        result.setRootElement(root);

        root.addContent(sGrpRoot);
        sGrpRoot.addContent(iGrpRoot);
        iGrpRoot.addContent(iDataRoot);

        return result;
    }

    private static XPath createXPath(String baseExpression) throws JDOMException {
        String expression = "/" + baseExpression.replaceAll("/", "/ns:");
        XPath xPath = XPath.newInstance(expression);
        xPath.addNamespace("ns", NS.getURI());
        return xPath;
    }

    private static Document buildDocument(String documentText) throws JDOMException, IOException {
        return new SAXBuilder().build(new ByteArrayInputStream(documentText.getBytes()));
    }
}
