/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * This class is parser for the HTTP header.
 *
 *  Content-Line : text/html ; charset=UTF-8 ; ...
 *  ------------  -----------   ------------------
 *       ^            ^               ^
 *       |            |               |
 *       |            |               +------ parameters
 *       |            |
 *       |            +------ value
 *       |
 *       +----- name
 *
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpHeaderParser {

    private byte[] header = null;
    private int length = 0;
    private int offset = 0;

    /**
     *
     */
    public HttpHeaderParser() {

    }

    /**
     * @param header
     * @return
     */
    public Map<String, String> parse(final byte[] header) {
        if (header == null || header.length == 0) {
            return Collections.emptyMap();
        }

        final Map<String, String> headers = new HashMap<>();
        final BiConsumer<String, String> listener = (name, value) -> {
            headers.put(name, value);
        };

        parse(listener, header);
        return headers;
    }

    /**
     * @param listener
     * @param header
     */
    public void parse(final BiConsumer<String, String> listener, byte[] header) {
        this.header = header;
        this.offset = 0;
        this.length = header.length;
        String line = null;
        String name = null;
        final StringBuffer value = new StringBuffer();
        while (hasByte()) {
            line = readLine();
            // remove empty line
            while (hasByte() && (line == null || line.length() == 0)) {
                line = readLine();
            }

            if (line == null || line.length() == 0) {
                break;
            }

            if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
                // folded value
                if (value != null) {
                    value.append(' ');
                    value.append(line.substring(1)); // remove fold mark(' ' or '\t')
                }
                continue;
            }

            if (name != null) {
                putHeader(listener, name, value.toString());
                value.setLength(0);
            }
            value.ensureCapacity(line.length());
            int colon = line.indexOf(":");
            if (colon != -1) {
                name = line.substring(0, colon);
                value.append(line.substring(colon + 1));
            }
        }

        if (name != null) {
            putHeader(listener, name, value.toString());
        }
    }

    /**
     * @param listener
     * @param name
     * @param value
     */
    private void putHeader(final BiConsumer<String, String> listener, String name, String value) {
        listener.accept(name.trim(), value.trim());
    }


    /**
     * @return
     */
    private boolean hasByte() {
        return this.offset < this.length;
    }


    /**
     * @return
     */
    private String readLine() {
        int l = offset;
        while (hasByte() && header[offset] != '\n') {
            offset++;
        }

        // remove '\n'
        int h = offset - 1;

        // remove '\r' if necessary
        if (offset > 0 && header[offset - 1] == '\r') {
            h--;
        }

        // skip '\n'
        offset++;
        return extract(l, h);
    }

    /**
     * @param l
     * @param h
     * @return
     */
    private String extract(int l, int h) {
        if (l > h) {
            return null;
        }
        return new String(header, l, h - l + 1);
    }

}
