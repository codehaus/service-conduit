/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 */
package org.sca4j.tests.autowire;

import java.util.List;

import org.osoa.sca.annotations.Reference;

public class DefaultSubmissionProcessor implements SubmissionProcessor {
    
    @Reference protected List<Rule<Payment>> rules;
    @Reference protected IdGenerator submissionIdGenerator;
    @Reference protected IdGenerator instructionIdGenerator;
    @Reference protected Parser parser;

    public void process() {
        submissionIdGenerator.nextId();
        instructionIdGenerator.nextId();
        Payment payment = null;
        while((payment = parser.next()) != null) {
            for (Rule<Payment> rule  : rules) {
                rule.execute(payment);
            }
        }

    }

}
