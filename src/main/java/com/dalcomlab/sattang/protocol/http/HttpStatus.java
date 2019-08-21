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

import java.util.HashMap;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public final class HttpStatus {

    /**
     * 100–199 Codes in the 100s indicate that the client should respond with some other action.
     * 200–299 Codes in the 200s indicate that the request was successful.
     * 300–399 Codes in the 300s indicate that the resource have moved.
     * 400–499 Codes in the 400s indicate an error by the client.
     * 500–599 Codes in the 500s indicate an error by the server.
     */
    public enum Category {
        INFORMATION,
        SUCCESS,
        REDIRECT,
        CLIENT_ERROR,
        SERVER_ERROR,
        UNKNOWN
    }

    public static final HttpStatus CONTINUE = new HttpStatus(100, "Continue");
    public static final HttpStatus SWITCHING_PROTOCOLS = new HttpStatus(101, "Switching Protocols");
    public static final HttpStatus PROCESSING = new HttpStatus(102, "Processing");
    public static final HttpStatus OK = new HttpStatus(200, "OK");
    public static final HttpStatus CREATED = new HttpStatus(201, "Created");
    public static final HttpStatus ACCEPTED = new HttpStatus(202, "Accepted");
    public static final HttpStatus NON_AUTHORITATIVE_INFORMATION = new HttpStatus(203, "Non-Authoritative Information");
    public static final HttpStatus NO_CONTENT = new HttpStatus(204, "No Content");
    public static final HttpStatus RESET_CONTENT = new HttpStatus(205, "Reset Content");
    public static final HttpStatus PARTIAL_CONTENT = new HttpStatus(206, "Partial Content");
    public static final HttpStatus MULTI_STATUS = new HttpStatus(207, "Multi-Status");
    public static final HttpStatus MULTIPLE_CHOICES = new HttpStatus(300, "Multiple Choices");
    public static final HttpStatus MOVED_PERMANENTLY = new HttpStatus(301, "Moved Permanently");
    public static final HttpStatus FOUND = new HttpStatus(302, "Found");
    public static final HttpStatus SEE_OTHER = new HttpStatus(303, "See Other");
    public static final HttpStatus NOT_MODIFIED = new HttpStatus(304, "Not Modified");
    public static final HttpStatus USE_PROXY = new HttpStatus(305, "Use Proxy");
    public static final HttpStatus TEMPORARY_REDIRECT = new HttpStatus(307, "Temporary Redirect");
    public static final HttpStatus PERMANENT_REDIRECT = new HttpStatus(308, "Permanent Redirect");
    public static final HttpStatus BAD_REQUEST = new HttpStatus(400, "Bad InboundChannel");
    public static final HttpStatus UNAUTHORIZED = new HttpStatus(401, "Unauthorized");
    public static final HttpStatus PAYMENT_REQUIRED = new HttpStatus(402, "Payment Required");
    public static final HttpStatus FORBIDDEN = new HttpStatus(403, "Forbidden");
    public static final HttpStatus NOT_FOUND = new HttpStatus(404, "Not Found");
    public static final HttpStatus METHOD_NOT_ALLOWED = new HttpStatus(405, "Method Not Allowed");
    public static final HttpStatus NOT_ACCEPTABLE = new HttpStatus(406, "Not Acceptable");
    public static final HttpStatus PROXY_AUTHENTICATION_REQUIRED = new HttpStatus(407, "Proxy Authentication Required");
    public static final HttpStatus REQUEST_TIMEOUT = new HttpStatus(408, "InboundChannel Timeout");
    public static final HttpStatus CONFLICT = new HttpStatus(409, "Conflict");
    public static final HttpStatus GONE = new HttpStatus(410, "Gone");
    public static final HttpStatus LENGTH_REQUIRED = new HttpStatus(411, "Length Required");
    public static final HttpStatus PRECONDITION_FAILED = new HttpStatus(412, "Precondition Failed");
    public static final HttpStatus REQUEST_ENTITY_TOO_LARGE = new HttpStatus(413, "InboundChannel Entity Too Large");
    public static final HttpStatus REQUEST_URI_TOO_LONG = new HttpStatus(414, "InboundChannel-URI Too Long");
    public static final HttpStatus UNSUPPORTED_MEDIA_TYPE = new HttpStatus(415, "Unsupported Media Type");
    public static final HttpStatus REQUESTED_RANGE_NOT_SATISFIABLE = new HttpStatus(416, "Requested Range Not Satisfiable");
    public static final HttpStatus EXPECTATION_FAILED = new HttpStatus(417, "Expectation Failed");
    public static final HttpStatus MISDIRECTED_REQUEST = new HttpStatus(421, "Misdirected InboundChannel");
    public static final HttpStatus UNPROCESSABLE_ENTITY = new HttpStatus(422, "Unprocessable Entity");
    public static final HttpStatus LOCKED = new HttpStatus(423, "Locked");
    public static final HttpStatus FAILED_DEPENDENCY = new HttpStatus(424, "Failed Dependency");
    public static final HttpStatus UNORDERED_COLLECTION = new HttpStatus(425, "Unordered Collection");
    public static final HttpStatus UPGRADE_REQUIRED = new HttpStatus(426, "Upgrade Required");
    public static final HttpStatus PRECONDITION_REQUIRED = new HttpStatus(428, "Precondition Required");
    public static final HttpStatus TOO_MANY_REQUESTS = new HttpStatus(429, "Too Many Requests");
    public static final HttpStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpStatus(431, "InboundChannel Header Fields Too Large");
    public static final HttpStatus INTERNAL_SERVER_ERROR = new HttpStatus(500, "Internal Server Error");
    public static final HttpStatus NOT_IMPLEMENTED = new HttpStatus(501, "Not Implemented");
    public static final HttpStatus BAD_GATEWAY = new HttpStatus(502, "Bad Gateway");
    public static final HttpStatus SERVICE_UNAVAILABLE = new HttpStatus(503, "Service Unavailable");
    public static final HttpStatus GATEWAY_TIMEOUT = new HttpStatus(504, "Gateway Timeout");
    public static final HttpStatus HTTP_VERSION_NOT_SUPPORTED = new HttpStatus(505, "HTTP Version Not Supported");
    public static final HttpStatus VARIANT_ALSO_NEGOTIATES = new HttpStatus(506, "Variant Also Negotiates");
    public static final HttpStatus INSUFFICIENT_STORAGE = new HttpStatus(507, "Insufficient Storage");
    public static final HttpStatus NOT_EXTENDED = new HttpStatus(510, "Not Extended");
    public static final HttpStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpStatus(511, "Network Authentication Required");

    private static final Map<Integer, HttpStatus> map = new HashMap<>();

    static {
        map.put(CONTINUE.getCode(), CONTINUE);
        map.put(SWITCHING_PROTOCOLS.getCode(), SWITCHING_PROTOCOLS);
        map.put(PROCESSING.getCode(), PROCESSING);
        map.put(CREATED.getCode(), CREATED);
        map.put(ACCEPTED.getCode(), ACCEPTED);
        map.put(NON_AUTHORITATIVE_INFORMATION.getCode(), NON_AUTHORITATIVE_INFORMATION);
        map.put(NO_CONTENT.getCode(), NO_CONTENT);
        map.put(RESET_CONTENT.getCode(), RESET_CONTENT);
        map.put(PARTIAL_CONTENT.getCode(), PARTIAL_CONTENT);
        map.put(MULTI_STATUS.getCode(), MULTI_STATUS);
        map.put(MULTIPLE_CHOICES.getCode(), MULTIPLE_CHOICES);
        map.put(MOVED_PERMANENTLY.getCode(), MOVED_PERMANENTLY);
        map.put(FOUND.getCode(), FOUND);
        map.put(SEE_OTHER.getCode(), SEE_OTHER);
        map.put(NOT_MODIFIED.getCode(), NOT_MODIFIED);
        map.put(USE_PROXY.getCode(), USE_PROXY);
        map.put(TEMPORARY_REDIRECT.getCode(), TEMPORARY_REDIRECT);
        map.put(PERMANENT_REDIRECT.getCode(), PERMANENT_REDIRECT);
        map.put(BAD_REQUEST.getCode(), BAD_REQUEST);
        map.put(UNAUTHORIZED.getCode(), UNAUTHORIZED);
        map.put(PAYMENT_REQUIRED.getCode(), PAYMENT_REQUIRED);
        map.put(FORBIDDEN.getCode(), FORBIDDEN);
        map.put(NOT_FOUND.getCode(), NOT_FOUND);
        map.put(METHOD_NOT_ALLOWED.getCode(), METHOD_NOT_ALLOWED);
        map.put(NOT_ACCEPTABLE.getCode(), NOT_ACCEPTABLE);
        map.put(PROXY_AUTHENTICATION_REQUIRED.getCode(), PROXY_AUTHENTICATION_REQUIRED);
        map.put(REQUEST_TIMEOUT.getCode(), REQUEST_TIMEOUT);
        map.put(CONFLICT.getCode(), CONFLICT);
        map.put(GONE.getCode(), GONE);
        map.put(LENGTH_REQUIRED.getCode(), LENGTH_REQUIRED);
        map.put(PRECONDITION_FAILED.getCode(), PRECONDITION_FAILED);
        map.put(REQUEST_ENTITY_TOO_LARGE.getCode(), REQUEST_ENTITY_TOO_LARGE);
        map.put(REQUEST_URI_TOO_LONG.getCode(), REQUEST_URI_TOO_LONG);
        map.put(UNSUPPORTED_MEDIA_TYPE.getCode(), UNSUPPORTED_MEDIA_TYPE);
        map.put(REQUESTED_RANGE_NOT_SATISFIABLE.getCode(), REQUESTED_RANGE_NOT_SATISFIABLE);
        map.put(EXPECTATION_FAILED.getCode(), EXPECTATION_FAILED);
        map.put(MISDIRECTED_REQUEST.getCode(), MISDIRECTED_REQUEST);
        map.put(UNPROCESSABLE_ENTITY.getCode(), UNPROCESSABLE_ENTITY);
        map.put(LOCKED.getCode(), LOCKED);
        map.put(FAILED_DEPENDENCY.getCode(), FAILED_DEPENDENCY);
        map.put(UNORDERED_COLLECTION.getCode(), UNORDERED_COLLECTION);
        map.put(UPGRADE_REQUIRED.getCode(), UPGRADE_REQUIRED);
        map.put(PRECONDITION_REQUIRED.getCode(), PRECONDITION_REQUIRED);
        map.put(TOO_MANY_REQUESTS.getCode(), TOO_MANY_REQUESTS);
        map.put(REQUEST_HEADER_FIELDS_TOO_LARGE.getCode(), REQUEST_HEADER_FIELDS_TOO_LARGE);
        map.put(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR);
        map.put(NOT_IMPLEMENTED.getCode(), NOT_IMPLEMENTED);
        map.put(BAD_GATEWAY.getCode(), BAD_GATEWAY);
        map.put(SERVICE_UNAVAILABLE.getCode(), SERVICE_UNAVAILABLE);
        map.put(GATEWAY_TIMEOUT.getCode(), GATEWAY_TIMEOUT);
        map.put(HTTP_VERSION_NOT_SUPPORTED.getCode(), HTTP_VERSION_NOT_SUPPORTED);
        map.put(VARIANT_ALSO_NEGOTIATES.getCode(), VARIANT_ALSO_NEGOTIATES);
        map.put(INSUFFICIENT_STORAGE.getCode(), INSUFFICIENT_STORAGE);
        map.put(NOT_EXTENDED.getCode(), NOT_EXTENDED);
        map.put(NETWORK_AUTHENTICATION_REQUIRED.getCode(), NETWORK_AUTHENTICATION_REQUIRED);
    }

    private final int code;
    private final String message;


    /**
     * @param code
     * @param message
     */
    private HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return
     */
    public int getCode() {
        return this.code;
    }

    /**
     * @return
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * 100–199 Codes in the 100s indicate that the client should respond with some other action.
     * 200–299 Codes in the 200s indicate that the request was successful.
     * 300–399 Codes in the 300s indicate that the resource have moved.
     * 400–499 Codes in the 400s indicate an error by the client.
     * 500–599 Codes in the 500s indicate an error by the server.
     *
     * @return
     */
    public Category getCategory() {
        if (is1xx()) {
            return Category.INFORMATION;
        }

        if (is2xx()) {
            return Category.SUCCESS;
        }

        if (is3xx()) {
            return Category.REDIRECT;
        }

        if (is4xx()) {
            return Category.CLIENT_ERROR;
        }

        if (is5xx()) {
            return Category.SERVER_ERROR;
        }
        return Category.UNKNOWN;
    }


    /**
     * @return
     */
    public boolean is1xx() {
        return code >= 100 && code < 200;
    }

    /**
     * @return
     */
    public boolean is2xx() {
        return code >= 200 && code < 300;
    }

    /**
     * @return
     */
    public boolean is3xx() {
        return code >= 300 && code < 400;
    }

    /**
     * @return
     */
    public boolean is4xx() {
        return code >= 400 && code < 500;
    }

    /**
     * @return
     */
    public boolean is5xx() {
        return code >= 500 && code < 600;
    }

    /**
     * @return
     */
    public boolean isInformation() {
        return getCategory() == Category.INFORMATION;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return getCategory() == Category.SUCCESS;
    }

    /**
     * @return
     */
    public boolean isRedirect() {
        return getCategory() == Category.REDIRECT;
    }

    /**
     * @return
     */
    public boolean isClientError() {
        return getCategory() == Category.CLIENT_ERROR;
    }

    /**
     * @return
     */
    public boolean isServerError() {
        return getCategory() == Category.SERVER_ERROR;
    }

}
