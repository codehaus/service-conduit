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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package tests.rs;

import junit.framework.TestCase;

import org.oasisopen.sca.annotation.Property;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @version $Rev: 5444 $ $Date: 2008-09-19 15:57:03 +0100 (Fri, 19 Sep 2008) $
 */
public class TestClient extends TestCase {
    
    @Property public String url;
    
    public void testOperations() throws Exception {
        
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        
        Book book = new Book();
        book.isbn = "1234";
        book.name = "Some book";
        
        webResource.put(book);
        
        BookList bookList = webResource.get(BookList.class);
        assertEquals(1, bookList.books.size());
        
        book = bookList.books.iterator().next();
        assertEquals("1234", book.isbn);
        assertEquals("Some book", book.name);
        
        book.name = "Some other book";
        webResource.post(book);
        
        webResource = client.resource(url + "/1234");
        book = webResource.get(Book.class);
        assertEquals("1234", book.isbn);
        assertEquals("Some other book", book.name);
        
        webResource.delete();

        webResource = client.resource(url);
        bookList = webResource.get(BookList.class);
        assertEquals(0, bookList.books.size());
        
        
    }
   
}
