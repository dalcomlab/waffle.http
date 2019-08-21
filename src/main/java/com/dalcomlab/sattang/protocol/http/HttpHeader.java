/*
 * Copyright SATTANG 2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.dalcomlab.sattang.protocol.http;

import com.dalcomlab.sattang.protocol.http.decoder.HttpAttributeDecoder;
import com.dalcomlab.sattang.protocol.http.decoder.MimeHeaderDecoder;

import java.util.*;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpHeader {

    /**
     * The HTTP header field names. This code is copied from a Spring framework.
     */
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-InboundChannel-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-InboundChannel-Method";
    public static final String AGE = "Age";
    public static final String ALLOW = "Allow";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";
    public static final String DATE = "Date";
    public static final String ETAG = "Etag";
    public static final String EXPECT = "Expect";
    public static final String EXPIRES = "Expires";
    public static final String FROM = "From";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_RANGE = "If-Range";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LINK = "Link";
    public static final String LOCATION = "Location";
    public static final String MAX_FORWARDS = "Max-Forwards";
    public static final String ORIGIN = "Origin";
    public static final String PRAGMA = "Pragma";
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String RANGE = "Range";
    public static final String REFERER = "Referer";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SET_COOKIE2 = "Set-Cookie2";
    public static final String TE = "Te";
    public static final String TRAILER = "Trailer";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";
    public static final String USER_AGENT = "User-Agent";
    public static final String VARY = "Vary";
    public static final String VIA = "Via";
    public static final String WARNING = "Warning";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    public static final String X_DISABLE_PUSH = "X-Disable-Push";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";
    public static final String X_FORWARDED_SERVER = "X-Forwarded-Server";
    public static final String X_FRAME_OPTIONS = "X-Frame-Options";
    public static final String X_XSS_PROTECTION = "X-Xss-Protection";

    public static final String SEC_WEB_SOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";
    public static final String SEC_WEB_SOCKET_KEY = "Sec-WebSocket-Key";
    public static final String SEC_WEB_SOCKET_KEY1 = "Sec-WebSocket-Key1";
    public static final String SEC_WEB_SOCKET_KEY2 = "Sec-WebSocket-Key2";
    public static final String SEC_WEB_SOCKET_LOCATION = "Sec-WebSocket-Location";
    public static final String SEC_WEB_SOCKET_ORIGIN = "Sec-WebSocket-Origin";
    public static final String SEC_WEB_SOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    public static final String SEC_WEB_SOCKET_VERSION = "Sec-WebSocket-Version";

    // Header names are not case sensitive.
    // From RFC 2616 - "Hypertext Transfer Protocol -- HTTP/1.1", Section 4.2, "Message Headers":
    // Each header field consists of a name followed by a colon (":") and the field value.
    // Field names are case-insensitive
    private Map<String, List<String>> headers = new TreeMap();
    private boolean headerNameLowerCase = false;

    /**
     * @param headerNameLowerCase
     */
    public HttpHeader(boolean headerNameLowerCase) {
        this.headerNameLowerCase = headerNameLowerCase;
    }

    /**
     * @param headers
     * @param headerNameLowerCase
     */
    public HttpHeader(Map<String, String> headers, boolean headerNameLowerCase) {
        assert (headers != null) : "the headers cannot be null.";
        this.headerNameLowerCase = headerNameLowerCase;
        headers.forEach(this::addHeader);
    }

    /**
     * Returns the accept in this http header.
     *
     * @return
     */
    public String getAccept() {
        return getHeader(ACCEPT);
    }

    /**
     * Returns the accept charset in this http header.
     *
     * @return
     */
    public String getAcceptCharset() {
        return getHeader(ACCEPT_CHARSET);
    }

    /**
     * Returns the accept encoding in this http header.
     *
     * @return
     */
    public String getAcceptEncoding() {
        return getHeader(ACCEPT_ENCODING);
    }

    /**
     * Returns the accept language in this http header.
     *
     * @return
     */
    public String getAcceptLanguage() {
        return getHeader(ACCEPT_LANGUAGE);
    }

    /**
     * Returns the accept ranges in this http header.
     *
     * @return
     */
    public String getAcceptRanges() {
        return getHeader(ACCEPT_RANGES);
    }


    /**
     * Returns the access-control-allow-credentials in this http header.
     *
     * @return
     */
    public String getAccessControlAllowCredentials() {
        return getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }

    /**
     * Returns the "Access-Control-Allow-Headers" in this http header.
     *
     * @return
     */
    public String getAccessControlAllowHeaders() {
        return getHeader(ACCESS_CONTROL_ALLOW_HEADERS);

    }

    /**
     * Returns the "Access-Control-Allow-Methods" in this http header.
     *
     * @return
     */
    public String getAccessControlAllowMethods() {
        return getHeader(ACCESS_CONTROL_ALLOW_METHODS);
    }

    /**
     * Returns the "Access-Control-Allow-Origin" in this http header.
     *
     * @return
     */
    public String getAccessControlAllowOrigin() {
        return getHeader(ACCESS_CONTROL_ALLOW_ORIGIN);

    }

    /**
     * Returns the "Access-Control-Expose-Headers" in this http header.
     *
     * @return
     */
    public String getAccessControlExposeHeaders() {
        return getHeader(ACCESS_CONTROL_EXPOSE_HEADERS);

    }

    /**
     * Returns the "Access-Control-Max-Age" in this http header.
     *
     * @return
     */
    public String getAccessControlMaxAge() {
        return getHeader(ACCESS_CONTROL_MAX_AGE);

    }

    /**
     * Returns the "Access-Control-InboundChannel-Headers" in this http header.
     *
     * @return
     */
    public String getAccessControlRequestHeaders() {
        return getHeader(ACCESS_CONTROL_REQUEST_HEADERS);

    }

    /**
     * Returns the "Access-Control-InboundChannel-Method" in this http header.
     *
     * @return
     */
    public String getAccessControlRequestMethod() {
        return getHeader(ACCESS_CONTROL_REQUEST_METHOD);

    }

    /**
     * Returns the "Age" in this http header.
     *
     * @return
     */
    public String getAge() {
        return getHeader(AGE);

    }

    /**
     * Returns the "Allow" in this http header.
     *
     * @return
     */
    public String getAllow() {
        return getHeader(ALLOW);

    }


    /**
     * Returns the "Authorization" in this http header.
     *
     * @return
     */
    public String getAuthorization() {
        return getHeader(AUTHORIZATION);

    }

    /**
     * Returns the "Cache-Control" in this http header.
     *
     * @return
     */
    public String getCacheControl() {
        return getHeader(CACHE_CONTROL);

    }

    /**
     * Returns the "Connection" in this http header.
     *
     * @return
     */
    public String getConnection() {
        return getHeader(CONNECTION);

    }

    /**
     * Returns the "Content-Encoding" in this http header.
     *
     * @return
     */
    public String getContentEncoding() {
        return getHeader(CONTENT_ENCODING);

    }

    /**
     * Returns the content disposition in this http header.
     *
     * @return
     */
    public String getContentDisposition() {
        return getHeader(CONTENT_DISPOSITION);
    }

    /**
     * Sets the content disposition in this http header.
     *
     * @param contentDisposition
     */
    public void setContentDisposition(String contentDisposition) {
        addHeader(CONTENT_DISPOSITION, contentDisposition);
    }

    /**
     * Returns the "Content-Language" in this http header.
     *
     * @return
     */
    public String getContentLanguage() {
        return getHeader(CONTENT_LANGUAGE);
    }


    /**
     * Return the length of the body in bytes in this http header.
     *
     * @return the length of the body in bytes, or -1 if the content-length is unknown.
     */
    public long getContentLength() {
        String value = getHeader(CONTENT_LENGTH);
        if (value == null) {
            return -1;
        }
        return Long.parseLong(value);
    }

    /**
     * Set the length of the body in bytes in this http header.
     *
     * @param length
     */
    public void setContentLength(long length) {
        addHeader(CONTENT_LENGTH, Long.toString(length));
    }


    /**
     * Returns the "Content-Location" in this http header.
     *
     * @return
     */
    public String getContentLocation() {
        return getHeader(CONTENT_LOCATION);
    }


    /**
     * Returns the "Content-Range" in this http header.
     *
     * @return
     */
    public String getContentRange() {
        return getHeader(CONTENT_RANGE);
    }


    /**
     * Returns the "Content-Type" in this http header.
     *
     * @return
     */
    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    /**
     * Returns the "Cookie" in this http header.
     *
     * @return
     */
    public String getCookie() {
        return getHeader(COOKIE);
    }


    /**
     * Returns the "Date" in this http header.
     *
     * @return
     */
    public String getDate() {
        return getHeader(DATE);
    }

    /**
     * Returns the "Etag" in this http header.
     *
     * @return
     */
    public String getEtag() {
        return getHeader(ETAG);
    }

    /**
     * Returns the "Expect" in this http header.
     *
     * @return
     */
    public String getExpect() {
        return getHeader(EXPECT);
    }

    /**
     * Returns the "Expires" in this http header.
     *
     * @return
     */
    public String getExpires() {
        return getHeader(EXPIRES);
    }

    /**
     * Returns the "From" in this http header.
     *
     * @return
     */
    public String getFrom() {
        return getHeader(FROM);
    }

    /**
     * Returns the "Host" in this http header.
     *
     * @return
     */
    public String getHost() {
        return getHeader(HOST);
    }


    /**
     * Returns the "If-Match" in this http header.
     *
     * @return
     */
    public String getIfMatch() {
        return getHeader(IF_MATCH);
    }

    /**
     * Returns the "If-Modified-Since" in this http header.
     *
     * @return
     */
    public String getIfModifiedSince() {
        return getHeader(IF_MODIFIED_SINCE);
    }

    /**
     * Returns the "If-None-Match" in this http header.
     *
     * @return
     */
    public String getIfNoneMatch() {
        return getHeader(IF_NONE_MATCH);
    }

    /**
     * Returns the "If-Range" in this http header.
     *
     * @return
     */
    public String getIfRange() {
        return getHeader(IF_RANGE);
    }

    /**
     * Returns the "If-Unmodified-Since" in this http header.
     *
     * @return
     */
    public String getIfUnmodifiedSince() {
        return getHeader(IF_UNMODIFIED_SINCE);
    }

    /**
     * Returns the "Last-Modified" in this http header.
     *
     * @return
     */
    public String getLastModified() {
        return getHeader(LAST_MODIFIED);
    }

    /**
     * Returns the "Link" in this http header.
     *
     * @return
     */
    public String getLink() {
        return getHeader(LINK);
    }


    /**
     * Returns the "Location" in this http header.
     *
     * @return
     */
    public String getLocation() {
        return getHeader(LOCATION);
    }

    /**
     * Returns the "Max-Forwards" in this http header.
     *
     * @return
     */
    public String getMaxForwards() {
        return getHeader(MAX_FORWARDS);
    }

    /**
     * Returns the "Origin" in this http header.
     *
     * @return
     */
    public String getOrigin() {
        return getHeader(ORIGIN);
    }

    /**
     * Returns the "Pragma" in this http header.
     *
     * @return
     */
    public String getPragma() {
        return getHeader(PRAGMA);
    }


    /**
     * Returns the "Proxy-Authenticate" in this http header.
     *
     * @return
     */
    public String getProxyAuthenticate() {
        return getHeader(PROXY_AUTHENTICATE);
    }

    /**
     * Returns the "Proxy-Authorization" in this http header.
     *
     * @return
     */
    public String getProxyAuthorization() {
        return getHeader(PROXY_AUTHORIZATION);
    }


    /**
     * Returns the "Range" in this http header.
     *
     * @return
     */
    public String getRange() {
        return getHeader(RANGE);
    }

    /**
     * Returns the "Referer" in this http header.
     *
     * @return
     */
    public String getReferer() {
        return getHeader(REFERER);
    }


    /**
     * Returns the "Retry-After" in this http header.
     *
     * @return
     */
    public String getRetryAfter() {
        return getHeader(RETRY_AFTER);
    }

    /**
     * Returns the "Server" in this http header.
     *
     * @return
     */
    public String getServer() {
        return getHeader(SERVER);
    }


    /**
     * Returns the "Set-Cookie" in this http header.
     *
     * @return
     */
    public String getSetCookie() {
        return getHeader(SET_COOKIE);
    }

    /**
     * Returns the "Set-Cookie2" in this http header.
     *
     * @return
     */
    public String getSetCookie2() {
        return getHeader(SET_COOKIE2);
    }

    /**
     * Returns the "Te" in this http header.
     *
     * @return
     */
    public String getTe() {
        return getHeader(TE);
    }

    /**
     * Returns the "Trailer" in this http header.
     *
     * @return
     */
    public String getTrailer() {
        return getHeader(TRAILER);
    }


    /**
     * Returns the "Transfer-Encoding" in this http header.
     *
     * @return
     */
    public String getTransferEncoding() {
        return getHeader(TRANSFER_ENCODING);
    }

    /**
     * Set the transfer encoding in bytes in this http header.
     *
     * @param encoding
     */
    public void setTransferEncoding(String encoding) {
        addHeader(TRANSFER_ENCODING, encoding);
    }


    /**
     * Returns the "Upgrade" in this http header.
     *
     * @return
     */
    public String getUpgrade() {
        return getHeader(UPGRADE);
    }

    /**
     * Returns the "User-Agent" in this http header.
     *
     * @return
     */
    public String getUserAgent() {
        return getHeader(USER_AGENT);
    }


    /**
     * Returns the "Vary" in this http header.
     *
     * @return
     */
    public String getVary() {
        return getHeader(VARY);
    }

    /**
     * Returns the "Via" in this http header.
     *
     * @return
     */
    public String getVia() {
        return getHeader(VIA);
    }

    /**
     * Returns the "Warning" in this http header.
     *
     * @return
     */
    public String getWarning() {
        return getHeader(WARNING);
    }

    /**
     * Returns the "WWW-Authenticate" in this http header.
     *
     * @return
     */
    public String getWwwAuthenticate() {
        return getHeader(WWW_AUTHENTICATE);
    }




    /**
     * Returns the boundary in the content-type header.
     *
     * @return
     */

    public byte[] getBoundary() {
        String boundary = getAttribute(CONTENT_TYPE, "boundary");
        if (boundary == null) {
            return null;
        }
        return boundary.getBytes();
    }

    /**
     * Returns the names of the headers of this http header or multipart header.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this multipart.
     */
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }

        final List<String> values = getHeaders(name);
        if (values == null) {
            return null;
        }

        return values.get(0);
    }

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    public List<String> getHeaders(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }

        if (headerNameLowerCase) {
            name = name.toLowerCase();
        }

        return headers.get(name);
    }

    /**
     * Returns the header's map.
     *
     * @return
     */
    public Map<String, List<String>> getHeaderMaps() {
        return Collections.unmodifiableMap(headers);
    }


    /**
     * Adds the header's value.
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        if (name == null || name.length() == 0) {
            return;
        }

        if (headerNameLowerCase) {
            name = name.toLowerCase();
        }

        List<String> values = headers.get(name);

        if (values == null) {
            values = new ArrayList();
            headers.put(name, values);
        }

        values.add(value);

    }

    /**
     * @param name
     */
    public void removeHeader(String name) {
        if (name == null || name.length() == 0) {
            return;
        }

        if (headerNameLowerCase) {
            name = name.toLowerCase();
        }

        headers.remove(name);
    }

    /**
     * Returns the header's attribute, or, the header's value if the given attribute
     * is null, or null if the header does not exist.
     *
     * @param name
     * @param attribute
     * @return
     */
    public String getAttribute(String name, String attribute) {
        String header = getHeader(name);
        if (header == null || header.length() == 0) {
            return null;
        }


        HttpAttributeDecoder decoder = new HttpAttributeDecoder(null, (value) -> {
            return new MimeHeaderDecoder().decode(value);
        });

        Map<String, String> attributeMap = decoder.decode(header, ';');
        if (attributeMap == null) {
            return null;
        }

        return attributeMap.get(attribute);
    }

    /**
     *
     */
    public void clear() {
        headers.clear();
    }

    /**
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((name, value) -> {
            sb.append(name);
            sb.append(":");
            sb.append(value);
            sb.append("\r\n");
        });
        return sb.toString();
    }
}
