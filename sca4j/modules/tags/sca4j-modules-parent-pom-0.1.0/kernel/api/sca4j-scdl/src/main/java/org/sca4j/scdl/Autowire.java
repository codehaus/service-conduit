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
package org.sca4j.scdl;

/**
 * Denotes if autowire is on, off or inherited.
 *
 * @version $Rev: 584 $ $Date: 2007-07-25 19:56:12 +0100 (Wed, 25 Jul 2007) $
 */
public enum Autowire {
    ON,
    OFF,
    INHERITED;

    /**
     * Parse an autowire value.
     *
     * @param text the text to parse
     * @return INHERITED if the text is null or empty, ON if text is "true", otherwise OFF
     */
    public static Autowire fromString(String text) {
        if (text == null || text.length() == 0) {
            return INHERITED;
        } else if ("true".equalsIgnoreCase(text)) {
            return ON;
        } else {
            return OFF;
        }
    }
}
