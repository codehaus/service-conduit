/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.tutorials.webcalc.ui;

import org.osoa.sca.annotations.Reference;
import org.sca4j.tutorials.webcalc.calculator.CalculatorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Accepts a calculator form submission and forwards the request to the CalculatorService.
 *
 * @version $Revision$ $Date$
 */
public class CalculatorServlet extends HttpServlet {

    @Reference
    protected CalculatorService calculatorService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operationParam = request.getParameter("operation");
        double operand1 = Double.valueOf(request.getParameter("operand1"));
        double operand2 = Double.valueOf(request.getParameter("operand2"));
        String operation;
        double result;
        if ("add".equals(operationParam)) {
            operation = " + ";
            result = calculatorService.add(operand1, operand2);
        } else if ("subtract".equals(operationParam)) {
            operation = " - ";
            result = calculatorService.subtract(operand1, operand2);
        } else if ("multiply".equals(operationParam)) {
            operation = " * ";
            result = calculatorService.multiply(operand1, operand2);
        } else if ("divide".equals(operationParam)) {
            operation = " / ";
            result = calculatorService.divide(operand1, operand2);
        } else {
            throw new ServletException("Unknown operation type");
        }
        Writer out = response.getWriter();
        out.write("<html><head><title>SCA4J Web Calculator</title></head><body>");
        out.write("<h2>Calculator Result</h2>");
        out.write("<br>" + operand1 + operation + operand2 + " = " + result);
        out.write("</body></html>");
        out.flush();
        out.close();
    }

}
