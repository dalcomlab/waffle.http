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
public final class HttpMethod {

    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    public static final HttpMethod GET = new HttpMethod("GET");
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    public static final HttpMethod POST = new HttpMethod("POST");
    public static final HttpMethod PUT = new HttpMethod("PUT");
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    public static final HttpMethod TRACE = new HttpMethod("TRACE");
    public static final HttpMethod PROPFIND = new HttpMethod("PROPFIND");
    public static final HttpMethod PROPPATCH = new HttpMethod("PROPPATCH");
    public static final HttpMethod MKCOL = new HttpMethod("MKCOL");
    public static final HttpMethod COPY = new HttpMethod("COPY");
    public static final HttpMethod MOVE = new HttpMethod("MOVE");
    public static final HttpMethod LOCK = new HttpMethod("LOCK");
    public static final HttpMethod UNLOCK = new HttpMethod("UNLOCK");
    public static final HttpMethod ACL = new HttpMethod("ACL");
    public static final HttpMethod REPORT = new HttpMethod("REPORT");
    public static final HttpMethod VERSION_CONTROL = new HttpMethod("VERSION-CONTROL");
    public static final HttpMethod CHECKIN = new HttpMethod("CHECKIN");
    public static final HttpMethod CHECKOUT = new HttpMethod("CHECKOUT");
    public static final HttpMethod SEARCH = new HttpMethod("SEARCH");
    public static final HttpMethod MKWORKSPACE = new HttpMethod("MKWORKSPACE");
    public static final HttpMethod UPDATE = new HttpMethod("UPDATE");
    public static final HttpMethod LABEL = new HttpMethod("LABEL");
    public static final HttpMethod MERGE = new HttpMethod("MERGE");
    public static final HttpMethod BASELINE_CONTROL = new HttpMethod("BASELINE-CONTROL");
    public static final HttpMethod MKACTIVITY = new HttpMethod("MKACTIVITY");
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod CONNECT = new HttpMethod("CONNECT");

    private final String method;
    private static final Map<String, HttpMethod> map = new HashMap<>();

    static {
        map.put(OPTIONS.name(), OPTIONS);
        map.put(GET.name(), GET);
        map.put(HEAD.name(), HEAD);
        map.put(POST.name(), POST);
        map.put(PUT.name(), PUT);
        map.put(DELETE.name(), DELETE);
        map.put(TRACE.name(), TRACE);
        map.put(PROPFIND.name(), PROPFIND);
        map.put(PROPPATCH.name(), PROPPATCH);
        map.put(MKCOL.name(), MKCOL);
        map.put(COPY.name(), COPY);
        map.put(MOVE.name(), MOVE);
        map.put(LOCK.name(), LOCK);
        map.put(UNLOCK.name(), UNLOCK);
        map.put(ACL.name(), ACL);
        map.put(REPORT.name(), REPORT);
        map.put(VERSION_CONTROL.name(), VERSION_CONTROL);
        map.put(CHECKIN.name(), CHECKIN);
        map.put(CHECKOUT.name(), CHECKOUT);
        map.put(SEARCH.name(), SEARCH);
        map.put(MKWORKSPACE.name(), MKWORKSPACE);
        map.put(UPDATE.name(), UPDATE);
        map.put(LABEL.name(), LABEL);
        map.put(MERGE.name(), MERGE);
        map.put(BASELINE_CONTROL.toString(), BASELINE_CONTROL);
        map.put(MKACTIVITY.name(), MKACTIVITY);
        map.put(PATCH.name(), PATCH);
        map.put(CONNECT.name(), CONNECT);
    }

    /**
     * @param method
     */
    private HttpMethod(String method) {
        this.method = method;
    }

    /**
     * @return
     */
    public String name() {
        return method;
    }

    /**
     * @param name
     * @return
     */
    public static HttpMethod valueOf(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return map.get(name.trim().toUpperCase());
    }
}
