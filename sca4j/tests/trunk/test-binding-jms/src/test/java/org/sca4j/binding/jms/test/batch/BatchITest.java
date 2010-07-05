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
package org.sca4j.binding.jms.test.batch;

import org.oasisopen.sca.annotation.Reference;

import junit.framework.TestCase;

public class BatchITest extends TestCase {
    
    @Reference public BatchService batchService;
    
    public void testBatch() throws InterruptedException {
        
        System.setProperty("count", "0");
        batchService.send(new String[] {"one", "two", "three", "four", "five"});
        
        int counter = 0;
        while (true) {
            if (++counter == 10) {
                fail("Messages not propagated");
            }
            String count = System.getProperty("count");
            if (count != null && 5 == Integer.parseInt(count)) {
                break;
            }
            
            Thread.sleep(1000);
            
        }
    }

}
