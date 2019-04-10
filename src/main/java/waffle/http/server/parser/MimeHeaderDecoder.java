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
import java.util.function.Function;

/**
 * Parse a string using the RFC 2047 rules for an "encoded-word" type. This
 * encoding has the syntax :
 * encoded-word = "=?" charset "?" encoding "?" encoded-text "?="
 * <p>
 * <p>
 * http://dogmamix.com/MimeHeadersDecoder/
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class MimeHeaderDecoder {

    private static final int STATE_CHARTSET = 0;
    private static final int STATE_ENCODING = 1;
    private static final int STATE_TEXT = 2;

    /**
     * Mappings between MIME and Java charset. This code is copied from a tomcat.
     */
    private static final Map<String, String> MIME2JAVA = new HashMap<>();

    static {
        MIME2JAVA.put("iso-2022-cn", "ISO2022CN");
        MIME2JAVA.put("iso-2022-kr", "ISO2022KR");
        MIME2JAVA.put("utf-8", "UTF8");
        MIME2JAVA.put("utf8", "UTF8");
        MIME2JAVA.put("ja_jp.iso2022-7", "ISO2022JP");
        MIME2JAVA.put("ja_jp.eucjp", "EUCJIS");
        MIME2JAVA.put("euc-kr", "KSC5601");
        MIME2JAVA.put("euckr", "KSC5601");
        MIME2JAVA.put("us-ascii", "ISO-8859-1");
        MIME2JAVA.put("x-us-ascii", "ISO-8859-1");
    }

    /**
     * @param text
     * @return
     */
    public String decode(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        final StringBuilder sb = new StringBuilder(text.length());
        final List<String> words = parse(text);
        for (String word : words) {
            if (word.startsWith("=?") && word.endsWith("?=")) {
                sb.append(decodeWord(word));
            } else {
                if (!isLinearWhiteSpace(word)) {
                    sb.append(word);
                }
            }
        }

        return sb.toString();
    }


    /**
     * @param text
     * @return
     */
    public List<String> parse(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        final StringBuilder word = new StringBuilder(text.length());
        final List<String> words = new ArrayList<>();

        // the utility lambda function to add the string to the list, and then clear it.
        Runnable add = () -> {
            if (word.length() > 0)
                words.add(word.toString());
            word.setLength(0);
        };

        boolean encoded = false;
        word.append(text.charAt(0));
        for (int i = 1; i < text.length(); i++) {
            char a = text.charAt(i - 1);
            char b = text.charAt(i);
            if (!encoded) {
                if (a == '=' && b == '?') {
                    word.deleteCharAt(word.length() - 1); // remove the last '=' char
                    add.run();
                    word.append("=?");
                    encoded = true;
                } else {
                    word.append(b);
                }

            } else {
                if (a == '?' && b == '=') {
                    word.append('=');
                    add.run();
                    encoded = false;
                } else {
                    word.append(b);
                }
            }

        }
        add.run();
        return words;
    }

    /**
     * Decode the given word. the word must start with "=?" and end with "?=".
     *
     * @param word
     * @return
     */
    public String decodeWord(String word) {
        String charset = "";
        String encoding = "";
        final int length = word.length();
        final StringBuilder text = new StringBuilder(length);
        int state = STATE_CHARTSET;
        for (int i = 2; i < length - 2; i++) {
            char c = word.charAt(i);
            if (state == STATE_CHARTSET || state == STATE_ENCODING) {
                if (c == '?') {
                    state++;
                    continue;
                }
                if (state == STATE_CHARTSET)
                    charset += c;
                else
                    encoding += c;

            } else if (state == STATE_TEXT) {
                text.append(c);
            }
        }


        if (charset.length() == 0 || encoding.length() == 0) {
            return word;
        }

        String decodedWord = null;
        final Function<String, String> decoder = createDecoder(getCharset(charset), encoding);
        if (decoder != null) {
            decodedWord = decoder.apply(text.toString());
        }

        if (decodedWord == null) {
            decodedWord = word;
        }

        return decodedWord;
    }

    /**
     * Creates the decoder corresponding to the given encoding type.
     * The encoding type "B" means base64 encoding.
     * The encoding type "Q" means quoted printable encoding.
     *
     * @param charset
     * @param encoding
     * @return
     */
    private Function<String, String> createDecoder(String charset, String encoding) {
        // base64 decoder
        if (encoding.equalsIgnoreCase("B")) {
            return (text) -> {
                try {
                    return new String(Base64.getDecoder().decode(text), charset);
                } catch (Exception e) {
                    // ignore
                }
                return null;
            };
        }

        // quoted printable decoder
        if (encoding.equalsIgnoreCase("Q")) {
            return (text) -> {
                try {
                    return new String(QuotedPrintableDecoder.decode(text.getBytes()), charset);
                } catch (Exception e) {
                    // ignore
                }
                return null;
            };
        }

        return null;
    }


    /**
     * @param text
     * @return
     */
    private boolean isLinearWhiteSpace(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (" \t\r\n".indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param charset
     * @return
     */
    private String getCharset(String charset) {
        if (charset == null) {
            return null;
        }

        String mappedCharset = MIME2JAVA.get(charset.toLowerCase(Locale.ENGLISH));
        if (mappedCharset == null) {
            return charset;
        }
        return mappedCharset;
    }

}