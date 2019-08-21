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

package com.dalcomlab.sattang.protocol.ajp;

import java.util.HashMap;
import java.util.Map;

/**
 * Taken almost directly from org.apache.coyote.ajp.Constants
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpConstants {

    // Prefix codes for message types from server to container
    public static final byte JK_AJP13_FORWARD_REQUEST = 2;
    public static final byte JK_AJP13_SHUTDOWN = 7;
    public static final byte JK_AJP13_PING_REQUEST = 8;
    public static final byte JK_AJP13_CPING_REQUEST = 10;

    // Prefix codes for message types from container to server
    public static final byte JK_AJP13_SEND_BODY_CHUNK = 3;
    public static final byte JK_AJP13_SEND_HEADERS = 4;
    public static final byte JK_AJP13_END_RESPONSE = 5;
    public static final byte JK_AJP13_GET_BODY_CHUNK = 6;
    public static final byte JK_AJP13_CPONG_REPLY = 9;


    public final static String[] HTTP_METHODS = new String[28];
    public final static String[] HTTP_HEADERS = new String[15];
    public final static String[] ATTRIBUTES = new String[14];


    private final static Map<String, Integer> responseHeaderName2Code = new HashMap<>();
    private final static Map<Integer, String> responseHeaderCode2Name = new HashMap<>();

    static {
        responseHeaderName2Code.put("content-type",0xA001);
        responseHeaderName2Code.put("content-language",0xA002);
        responseHeaderName2Code.put("content-length",0xA003);
        responseHeaderName2Code.put("date",0xA004);
        responseHeaderName2Code.put("last-modified",0xA005);
        responseHeaderName2Code.put("location",0xA006);
        responseHeaderName2Code.put("set-cookie",0xA007);
        responseHeaderName2Code.put("set-cookie2",0xA008);
        responseHeaderName2Code.put("servlet-engine",0xA009);
        responseHeaderName2Code.put("status",0xA00A);
        responseHeaderName2Code.put("www-authenticate",0xA00B);
    }

    static {
        responseHeaderCode2Name.put(0xA001, "content-type");
        responseHeaderCode2Name.put(0xA002, "content-language");
        responseHeaderCode2Name.put(0xA003, "content-length");
        responseHeaderCode2Name.put(0xA004, "date");
        responseHeaderCode2Name.put(0xA005, "last-modified");
        responseHeaderCode2Name.put(0xA006, "location");
        responseHeaderCode2Name.put(0xA007, "set-cookie");
        responseHeaderCode2Name.put(0xA008, "set-cookie2");
        responseHeaderCode2Name.put(0xA009, "servlet-engine");
        responseHeaderCode2Name.put(0xA00A, "status");
        responseHeaderCode2Name.put(0xA00B, "www-authenticate");
    }

    static {
        HTTP_METHODS[1] = "OPTIONS";
        HTTP_METHODS[2] = "GET";
        HTTP_METHODS[3] = "HEAD";
        HTTP_METHODS[4] = "POST";
        HTTP_METHODS[5] = "PUT";
        HTTP_METHODS[6] = "DELETE";
        HTTP_METHODS[7] = "TRACE";
        HTTP_METHODS[8] = "PROPFIND";
        HTTP_METHODS[9] = "PROPPATCH";
        HTTP_METHODS[10] = "MKCOL";
        HTTP_METHODS[11] = "COPY";
        HTTP_METHODS[12] = "MOVE";
        HTTP_METHODS[13] = "LOCK";
        HTTP_METHODS[14] = "UNLOCK";
        HTTP_METHODS[15] = "ACL";
        HTTP_METHODS[16] = "REPORT";
        HTTP_METHODS[17] = "VERSION-CONTROL";
        HTTP_METHODS[18] = "CHECKIN";
        HTTP_METHODS[19] = "CHECKOUT";
        HTTP_METHODS[20] = "UNCHECKOUT";
        HTTP_METHODS[21] = "SEARCH";
        HTTP_METHODS[22] = "MKWORKSPACE";
        HTTP_METHODS[23] = "UPDATE";
        HTTP_METHODS[24] = "LABEL";
        HTTP_METHODS[25] = "MERGE";
        HTTP_METHODS[26] = "BASELINE_CONTROL";
        HTTP_METHODS[27] = "MKACTIVITY";

        HTTP_HEADERS[1] = "accept";
        HTTP_HEADERS[2] = "accept-charset";
        HTTP_HEADERS[3] = "accept-encoding";
        HTTP_HEADERS[4] = "accept-language";
        HTTP_HEADERS[5] = "authorization";
        HTTP_HEADERS[6] = "connection";
        HTTP_HEADERS[7] = "content-type";
        HTTP_HEADERS[8] = "content-length";
        HTTP_HEADERS[9] = "cookie";
        HTTP_HEADERS[10] = "cookie2";
        HTTP_HEADERS[11] = "host";
        HTTP_HEADERS[12] = "pragma";
        HTTP_HEADERS[13] = "referer";
        HTTP_HEADERS[14] = "agent";

        ATTRIBUTES[1] = "context";
        ATTRIBUTES[2] = "servlet_path";
        ATTRIBUTES[3] = "remote_user";
        ATTRIBUTES[4] = "auth_type";
        ATTRIBUTES[5] = "query_string";
        ATTRIBUTES[6] = "route";
        ATTRIBUTES[7] = "ssl_cert";
        ATTRIBUTES[8] = "ssl_cipher";
        ATTRIBUTES[9] = "ssl_session";
        ATTRIBUTES[10] = "req_attribute";
        ATTRIBUTES[11] = "ssl_key_size";
        ATTRIBUTES[12] = "secret";
        ATTRIBUTES[13] = "stored_method";
    }


    public final static int ATTRIBUTE_CODE_CONTEXT          = 0x01;
    public final static int ATTRIBUTE_CODE_SERVLET_PATH     = 0x02;
    public final static int ATTRIBUTE_CODE_REMOTE_USER      = 0x03;
    public final static int ATTRIBUTE_CODE_AUTH_TYPE        = 0x04;
    public final static int ATTRIBUTE_CODE_QUERY_STRING     = 0x05;
    public final static int ATTRIBUTE_CODE_ROUTE            = 0x06;
    public final static int ATTRIBUTE_CODE_SSL_CERT         = 0x07;
    public final static int ATTRIBUTE_CODE_SSL_CIPHER       = 0x08;
    public final static int ATTRIBUTE_CODE_SSL_SESSION      = 0x09;
    public final static int ATTRIBUTE_CODE_REQ_ATTRIBUTE    = 0x0A;
    public final static int ATTRIBUTE_CODE_KEY_SIZE         = 0x0B;
    public final static int ATTRIBUTE_CODE_SECRET           = 0x0C;
    public final static int ATTRIBUTE_CODE_STORED_METHOD    = 0x0D;
    public final static int ATTRIBUTE_CODE_DONE             = 0xFF;

    /**
     * @param code
     * @return
     */
    public static String getMethod(int code) {
        if (code == 0 || code >= HTTP_METHODS.length) {
            return "";
        }
        return HTTP_METHODS[code];
    }

    /**
     * @param code
     * @return
     */
    public static String getHeader(int code) {
        if (code == 0 || code >= HTTP_HEADERS.length) {
            return "";
        }
        return HTTP_HEADERS[code];
    }

    /**
     * @param code
     * @return
     */
    public static String getAttribute(int code) {
        if (code == 0 || code >= ATTRIBUTES.length) {
            return "";
        }
        return ATTRIBUTES[code];
    }

    /**
     *
     * @param code
     * @return
     */
    public static String getRequestHeaderName(int code) {
        return "";
    }

    /**
     *
     * @param name
     * @return
     */
    public static int getRequestHeaderCode(String name) {
        return 0;
    }

    /**
     *
     * @param name
     * @return
     */
    public static boolean isResponseHeader(String name) {
        return responseHeaderName2Code.containsKey(name);
    }

    /**
     *
     * @param code
     * @return
     */
    public static String getResponseHeaderName(int code) {
        return responseHeaderCode2Name.get(code);
    }

    /**
     *
     * @param name
     * @return
     */
    public static int getResponseHeaderCode(String name) {
        return responseHeaderName2Code.get(name);
    }
}
