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
package com.dalcomlab.sattang.protocol.http.decoder;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpAttributeDecoder {
    private static final Set<Character> UNICODE_WHITESPACES = new HashSet<>();

    static {
        UNICODE_WHITESPACES.add('\u0009');  // CHARACTER TABULATION, \t
        UNICODE_WHITESPACES.add('\n');      // PLAIN FEED (LF), \n -> new line
        UNICODE_WHITESPACES.add('\u000B');  // PLAIN TABULATION, \v -> new line
        UNICODE_WHITESPACES.add('\u000C');  // FORM FEED (FF) -> break page
        UNICODE_WHITESPACES.add('\r');      // CARRIAGE RETURN (CR), \r
        UNICODE_WHITESPACES.add('\u0020');  // SPACE
        UNICODE_WHITESPACES.add('\u0085');  // NEXT PLAIN (NEL) -> new line
        UNICODE_WHITESPACES.add('\u00A0');  //  NO-BREAK SPACE
        UNICODE_WHITESPACES.add('\u1680');  //  OGHAM SPACE MARK
        UNICODE_WHITESPACES.add('\u180E');  //  MONGOLIAN VOWEL SEPARATOR
        UNICODE_WHITESPACES.add('\u2000');  //  EN QUAD
        UNICODE_WHITESPACES.add('\u2001');  //  EM QUAD
        UNICODE_WHITESPACES.add('\u2002');  //  EN SPACE
        UNICODE_WHITESPACES.add('\u2003');  //  EM SPACE
        UNICODE_WHITESPACES.add('\u2004');  //  THREE-PER-EM SPACE
        UNICODE_WHITESPACES.add('\u2005');  //  FOUR-PER-EM SPACE
        UNICODE_WHITESPACES.add('\u2006');  //  SIX-PER-EM SPACE
        UNICODE_WHITESPACES.add('\u2007');  //  FIGURE SPACE
        UNICODE_WHITESPACES.add('\u2008');  //  PUNCTUATION SPACE
        UNICODE_WHITESPACES.add('\u2009');  //  THIN SPACE
        UNICODE_WHITESPACES.add('\u200A');  //  HAIR SPACE
        UNICODE_WHITESPACES.add('\u2028');  //  PLAIN SEPARATOR
        UNICODE_WHITESPACES.add('\u2029');  //  PARAGRAPH SEPARATOR
        UNICODE_WHITESPACES.add('\u202F');  //  NARROW NO-BREAK SPACE
        UNICODE_WHITESPACES.add('\u205F');  //  MEDIUM MATHEMATICAL SPACE
        UNICODE_WHITESPACES.add('\u3000');  //  IDEOGRAPHIC SPACE
    }

    private Function<String, String> nameDecoder = null;
    private Function<String, String> valueDecoder = null;
    private char[] text = null;
    private int length = 0;
    private int offset = 0;
    private char separator = 0;

    /**
     * Create new instance.
     */
    public HttpAttributeDecoder() {

    }

    /**
     * Create new instance with a decoder. the decoder apply to both a name and a value.
     *
     * @param decoder
     */
    public HttpAttributeDecoder(Function<String, String> decoder) {
        this(decoder, decoder);
    }

    /**
     * Creates new instance with a name decoder and a value decoder.
     *
     * @param nameDecoder
     * @param valueDecoder
     */
    public HttpAttributeDecoder(Function<String, String> nameDecoder, Function<String, String> valueDecoder) {
        this.nameDecoder = nameDecoder;
        this.valueDecoder = valueDecoder;
    }

    /**
     * Sets a name decoder.
     *
     * @param nameDecoder
     */
    public void setNameDecoder(Function<String, String> nameDecoder) {
        this.nameDecoder = nameDecoder;
    }

    /**
     * Sets a value decoder.
     *
     * @param valueDecoder
     */
    public void setValueDecoder(Function<String, String> valueDecoder) {
        this.valueDecoder = valueDecoder;
    }

    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separator
     * @return
     */
    public Map<String, String> decode(String text, char separator) {
        char[] array = text.toCharArray();
        return decode(array, 0, array.length, separator);
    }

    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separator
     * @return
     */
    public Map<String, String> decode(char[] text, int offset, int length, char separator) {
        if (text == null || length == 0) {
            return Collections.emptyMap();
        }

        final Map<String, String> parameters = new HashMap<>();
        final BiConsumer<String, String> listener = (name, value) -> {
            parameters.put(name, value);
        };

        decode(listener, text, offset, length, separator);
        return parameters;
    }

    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separator
     * @return
     */
    public Map<String, List<String>> decodeMultipleValue(String text, char separator) {
        if (text == null || text.length() == 0) {
            return Collections.emptyMap();
        }

        final Map<String, List<String>> parameters = new HashMap<>();
        final BiConsumer<String, String> listener = (name, value) -> {
            if (!parameters.containsKey(name)) {
                parameters.put(name, new ArrayList<>());
            }
            parameters.get(name).add(value);
        };

        char[] array = text.toCharArray();
        decode(listener, array, 0, array.length, separator);
        return parameters;
    }

    /**
     * @return
     */
    private boolean hasChar() {
        return this.offset < this.length;
    }


    /**
     * @return
     */
    private String parseName() {
        int l = offset;
        while (hasChar() && text[offset] != '=' && text[offset] != separator) {
            offset++;
        }
        int h = offset - 1;
        return extract(l, h);
    }

    /**
     * @return
     */
    private String parseValue() {
        int l = offset + 1; // skip '='
        char c;
        boolean quoted = false;
        while (hasChar()) {
            c = text[offset];
            if (!quoted && c == separator) {
                break;
            }

            if (c == '\"') {
                if ((offset - 1) >= 0 && text[offset - 1] != '\\') {
                    quoted = !quoted;
                }
            }
            offset++;
        }
        int h = offset - 1;
        offset++; // skip separator(e.g, ';')
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

        // trim leading white spaces.
        while (l < h && isWhitespace(text[l])) {
            l++;
        }

        // the text is consisted to only white spaces.
        if (isWhitespace(text[l])) {
            return null;
        }

        // trim trailing white spaces.
        while (h > l && isWhitespace(text[h])) {
            h--;
        }

        // strip away quotation marks.
        if (text[l] == '\"' && text[h] == '\"') {
            l++;
            h--;
        }

        if (l > h) {
            return null;
        }

        return new String(text, l, h - l + 1);
    }


    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param listener
     * @param text
     * @param separator
     */
    private void decode(BiConsumer<String, String> listener, char[] text, int offset, int length, char separator) {
        this.text = text;
        this.offset = offset;
        this.length = length;
        this.separator = separator;
        String name;
        String value;
        while (hasChar()) {
            name = parseName();
            value = parseValue();
            if (name != null) {
                putParameter(listener, name, value);
            }
        }
    }

    /**
     *
     * @param listener
     * @param name
     * @param value
     */
    private void putParameter(BiConsumer<String, String> listener, String name, String value) {
        if (nameDecoder != null) {
            name = nameDecoder.apply(name);
        }

        if (valueDecoder != null) {
            value = valueDecoder.apply(value);
        }

        listener.accept(name, value);
    }


    /**
     * @param c
     * @return
     */
    public boolean isWhitespace(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        return UNICODE_WHITESPACES.contains(c);
    }

}
