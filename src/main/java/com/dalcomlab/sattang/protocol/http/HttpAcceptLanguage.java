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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpAcceptLanguage {
    private final Locale locale;
    private final double quality;

    /**
     *
     * @param locale
     * @param quality
     */
    public HttpAcceptLanguage(Locale locale, double quality) {
        this.locale = locale;
        this.quality = quality;
    }

    /**
     *
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     *
     * @return
     */
    public double getQuality() {
        return quality;
    }

    /**
     * en; q= 0.05,fr;q=0.5"
     * @param buffer
     * @return
     */
    public static List<HttpAcceptLanguage> parse(ByteBuffer buffer) {
        if (buffer == null) {
            return Collections.emptyList();
        }
        List<HttpAcceptLanguage> locales = new ArrayList<>();
        String locale = "";
        String quality = "";
        State state = State.LOCALE;

        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == ';') {
                state = State.QUALITY;
            } else if (b == ',') {
                if (locale.length() > 0) {
                    locales.add(new HttpAcceptLanguage(Locale.forLanguageTag(locale), 0));
                    locale = "";
                    quality = "";
                }
                state = State.LOCALE;
            } else if (b == ' ' || b == '\t') {
                continue;
            } else {
                if (state == State.LOCALE) {
                    locale += (char)b;
                } else {
                    locale += (char)b;
                }
            }
        }

        return null;
    }

    private enum State {
        LOCALE,
        QUALITY
    };
}