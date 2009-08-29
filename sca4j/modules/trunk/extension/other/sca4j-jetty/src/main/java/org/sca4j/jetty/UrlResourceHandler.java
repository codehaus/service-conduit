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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.io.Buffer;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.io.WriterOutputStream;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.HttpMethods;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.log.Log;
import org.mortbay.resource.Resource;
import org.mortbay.util.TypeUtil;
import org.mortbay.util.URIUtil;


public class UrlResourceHandler extends AbstractHandler {
//    ContextHandler _context;
    Resource _baseResource;
    String[] _welcomeFiles = {"index.html"};
    MimeTypes _mimeTypes = new MimeTypes();
    ByteArrayBuffer _cacheControl;

    public void doStart() throws Exception {
//        ContextHandler.SContext scontext = ContextHandler.getCurrentContext();
//        _context = (scontext == null ? null : scontext.getContextHandler());
        super.doStart();
    }

    private Resource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/"))
            throw new MalformedURLException(path);

        Resource base = _baseResource;
//        if (base == null) {
//            if (_context == null)
//                return null;
//            base = _context.getBaseResource();
//            if (base == null)
//                return null;
//        }

        try {
            path = URIUtil.canonicalPath(path);
            return base.addPath(path);
        }
        catch (Exception e) {
            Log.ignore(e);
        }

        return null;
    }

    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
        String path_info = request.getPathInfo();
        if (path_info == null)
            return null;
        return getResource(path_info);
    }


    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        Request base_request = request instanceof Request ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        if (base_request.isHandled() || !request.getMethod().equals(HttpMethods.GET))
            return;

        Resource resource = getResource(request);

        if (resource == null || !resource.exists())
            return;

        // We are going to server something
        base_request.setHandled(true);

        if (resource.isDirectory()) {
            if (!request.getPathInfo().endsWith(URIUtil.SLASH)) {
                response.sendRedirect(URIUtil.addPaths(request.getRequestURI(), URIUtil.SLASH));
                return;
            }

            if (resource == null || !resource.exists() || resource.isDirectory()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // set some headers
        long last_modified = resource.lastModified();
        if (last_modified > 0) {
            long if_modified = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        Buffer mime = _mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null)
            mime = _mimeTypes.getMimeByExtension(request.getPathInfo());

        // set the headers
        doResponseHeaders(response, resource, mime != null ? mime.toString() : null);

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        }
        catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        // See if a short direct method can be used?
        if (out instanceof HttpConnection.Output) {
            // TODO file mapped buffers
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);
            ((HttpConnection.Output) out).sendContent(resource.getInputStream());
        } else {
            // Write content normally
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);
            resource.writeTo(out, 0, resource.length());
        }
    }

    /**
     * Set the response headers. This method is called to set the response headers such as content type and content length. May be extended to add
     * additional headers.
     *
     * @param response
     * @param resource
     * @param mimeType
     */
    protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
        if (mimeType != null)
            response.setContentType(mimeType);

        long length = resource.length();

        if (response instanceof Response) {
            HttpFields fields = ((Response) response).getHttpFields();

            if (length > 0)
                fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);

            if (_cacheControl != null)
                fields.put(HttpHeaders.CACHE_CONTROL_BUFFER, _cacheControl);
        } else {
            if (length > 0)
                response.setHeader(HttpHeaders.CONTENT_LENGTH, TypeUtil.toString(length));

            if (_cacheControl != null)
                response.setHeader(HttpHeaders.CACHE_CONTROL, _cacheControl.toString());
        }

    }
}

