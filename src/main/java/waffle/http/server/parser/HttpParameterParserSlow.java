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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpParameterParserSlow {
    private static Set<Character> UNICODE_WHITESPACES = new HashSet<>();
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

    /**
     * Create new instance.
     */
    public HttpParameterParserSlow() {

    }

    /**
     * Create new instance with a decoder. the decoder apply to both a name and a value.
     *
     * @param decoder
     */
    public HttpParameterParserSlow(Function<String, String> decoder) {
        this(decoder, decoder);
    }

    /**
     * Creates new instance with a name decoder and a value decoder.
     *
     * @param nameDecoder
     * @param valueDecoder
     */
    public HttpParameterParserSlow(Function<String, String> nameDecoder, Function<String, String> valueDecoder) {
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
    public Map<String, String> parseSingleValue(String text, char separator) {
        return parseSingleValue(text, "" + separator);
    }

    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separators
     * @return
     */
    public Map<String, String> parseSingleValue(String text, String separators) {
        final Map<String, String> parameters = new HashMap<>();
        parse((name, value) -> {
            parameters.put(name, value);
        }, text, separators);
        return parameters;
    }


    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separator
     * @return
     */
    public Map<String, List<String>> parseMultipleValue(String text, char separator) {
        return parseMultipleValue(text, "" + separator);
    }


    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param text
     * @param separators
     * @return
     */
    public Map<String, List<String>> parseMultipleValue(String text, String separators) {
        final Map<String, List<String>> parameters = new HashMap<>();
        parse((name, value) -> {
            if (!parameters.containsKey(name)) {
                parameters.put(name, new ArrayList<>());
            }
            parameters.get(name).add(value);
        }, text, separators);
        return parameters;
    }

    /**
     * Extracts a map of name/value pairs from the given string. Names are expected to be unique.
     *
     * @param parameters
     * @param text
     * @param separators
     */
    private void parse(BiConsumer<String, String> parameters, String text, String separators) {
        if (text == null || text.length() == 0) {
            return;
        }

        if (separators == null || separators.length() == 0) {
            return;
        }

        int length = text.length();
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean isName = true;
        boolean isQuote = false;
        boolean isEscape = false;
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (!isQuote && !isEscape) {
                // note that the value can contain the '=' char.
                if (isName && c == '=') {
                    value.setLength(0);
                    isName = false;
                    continue;
                }

                if (separators.indexOf(c) != -1) {
                    putParameter(parameters, name, value);
                    name.setLength(0);
                    value.setLength(0);
                    isName = true;
                    continue;
                }
            }

            if (!isEscape && c == '\"') {
                isQuote = !isQuote;
            }

            isEscape = (c == '\\') ? true : false;

            if (isName)
                name.append(c);
            else
                value.append(c);
        }

        putParameter(parameters, name, value);
    }

    /**
     * @param parameters
     * @param name
     * @param value
     */
    private void putParameter(BiConsumer<String, String> parameters, StringBuilder name, StringBuilder value) {
        if (parameters == null) {
            return;
        }

        assert(name != null);
        assert(value != null);

        String normalizedName = normalize(name.toString());
        if (normalizedName.isEmpty()) {
            return;
        }

        String normalizedValue = normalize(value.toString());
        if (nameDecoder != null) {
            normalizedName = nameDecoder.apply(normalizedName);
        }

        if (valueDecoder != null) {
            normalizedValue = valueDecoder.apply(normalizedValue);
        }

        parameters.accept(normalizedName, normalizedValue);
    }

    /**
     * @param text
     * @return
     */
    public String normalize(String text) {
        // String.trim() method doesn't support the Unicode white space. so we make it.
        String normalized = trim(text);
        return unquote(normalized);
    }

    /**
     * @param text
     * @return
     */
    public String trim(String text) {
        int length = text.length();
        int i = 0;
        while (i < length && isWhitespace(text.charAt(i))) {
            i++;
        }

        // case of text is all white space.
        if (i == length) {
            return "";
        }

        int j = length - 1;
        while (j >= 0 && isWhitespace(text.charAt(j))) {
            j--;
        }
        return text.substring(i, j + 1);
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

    /**
     * @param text
     * @return
     */
    public String unquote(String text) {
        int length = text.length();
        if (length > 1) {
            if (text.charAt(0) == '\"' &&
                    text.charAt(length - 1) == '\"') {
                text = text.substring(1, length - 1);
            }
        }
        return text;
    }
}