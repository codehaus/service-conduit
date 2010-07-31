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
package tests.rs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sca4j.api.annotation.scope.Composite;


@Path("/books")
@Composite
public class BookResource {
    
    private Map<String, Book> books = new HashMap<String, Book>();
    
    @PUT
    @Consumes("application/xml")
    public void add(Book book) {
        books.put(book.isbn, book);
        System.err.println("Book added");
    }
    
    @POST
    @Consumes("application/xml")
    public void amend(Book book) {
        books.put(book.isbn, book);
        System.err.println("Book amended");
    }
    
    @DELETE
    @Path("/{isbn}")
    public void remove(@PathParam("isbn") String isbn) {
        books.remove(isbn);
        System.err.println("Book deleted");
    }
    
    @GET
    @Produces("application/xml")
    public BookList list() {
       BookList bookList = new BookList();
       bookList.books.addAll(books.values());
       System.err.println("Book list called with size " + books.size());
       return bookList;
    }
    
    @GET
    @Produces("application/xml")
    @Path("/{isbn}")
    public Book get(@PathParam("isbn") String isbn) {
       return books.get(isbn);
    }

}
