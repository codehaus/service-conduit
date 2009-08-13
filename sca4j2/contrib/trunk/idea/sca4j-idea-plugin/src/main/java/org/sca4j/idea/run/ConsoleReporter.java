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
package org.sca4j.idea.run;

import com.intellij.execution.ui.ConsoleView;
import static com.intellij.execution.ui.ConsoleViewContentType.NORMAL_OUTPUT;
import org.apache.maven.surefire.report.AbstractReporter;

/**
 * Writes Surefire reports to a ConsoleView.
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class ConsoleReporter extends AbstractReporter {
    ConsoleView view;

    protected ConsoleReporter(ConsoleView view, Boolean trimStackTrace) {
        super(trimStackTrace);
        this.view = view;
    }

    public void writeMessage(String s) {
        view.print(s, NORMAL_OUTPUT);
        view.print("\n", NORMAL_OUTPUT);
    }
}
