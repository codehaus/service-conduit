package com.travelex.tgbp.rules.dynamic.execute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ExpressionBuilder {

    @SuppressWarnings("unchecked")
    public static List<Expression> build(String ruleDefinitionText) throws JDOMException, IOException {
        List<Expression> result = new ArrayList<Expression>();
        Document doc = buildDocument(ruleDefinitionText);
        Element ruleData = doc.getRootElement();
        List<Element> rules = ruleData.getChildren("rule");
        for (Element rule : rules) {
            result.add(new Expression(rule.getChildText("path"),
                    rule.getChildText("operator"),
                    rule.getChildText("type"),
                    rule.getChildText("data")));
        }
        return result;
    }


    private static Document buildDocument(String documentText) throws JDOMException, IOException {
        return new SAXBuilder().build(new ByteArrayInputStream(documentText.getBytes()));
    }

}
