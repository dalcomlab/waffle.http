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
/*
RequestUtil.java

public static Enumeration<Locale> getLocales(HttpServletRequest req)
  {
    String acceptLanguage = req.getHeader("Accept-Language");
    if (acceptLanguage == null)
    {
      List<Locale> def = new ArrayList();
      def.add(Locale.getDefault());
      return Collections.enumeration(def);
    }
    TreeMap<Double, List<String>> languages = new TreeMap();
    StringTokenizer languageTokenizer = new StringTokenizer(acceptLanguage, ",");
    while (languageTokenizer.hasMoreTokens())
    {
      String language = languageTokenizer.nextToken().trim();
      int qValueIndex = language.indexOf(';');
      int qIndex = language.indexOf('q');
      int equalIndex = language.indexOf('=');
      Double qValue = Double.valueOf(1.0D);
      if ((qValueIndex > -1) && (qValueIndex < qIndex) && (qIndex < equalIndex))
      {
        String qValueStr = language.substring(qValueIndex + 1);

        language = language.substring(0, qValueIndex);
        qValueStr = StringUtil.toLowerCase(qValueStr.trim());
        qValueIndex = qValueStr.indexOf('=');
        qValue = Double.valueOf(0.0D);
        if ((qValueStr.startsWith("q")) && (qValueIndex > -1))
        {
          qValueStr = qValueStr.substring(qValueIndex + 1);
          try
          {
            qValue = new Double(qValueStr.trim());
          }
          catch (NumberFormatException nfe) {}
        }
      }
      if ((!language.equals("*")) && (qValue.doubleValue() >= 5.0E-5D))
      {
        Double key = Double.valueOf(-qValue.doubleValue());
        List<String> locales = languages.containsKey(key) ? (List)languages.get(key) : new ArrayList();
        locales.add(language);
        languages.put(key, locales);
      }
    }
    if (languages.size() == 0)
    {
      List<String> locales = new ArrayList();
      locales.add("en");

      languages.put(Double.valueOf(1.0D), locales);
    }
    List<Locale> locales = new ArrayList();
    for (List<String> entry : languages.values()) {
      for (String language : entry)
      {
        String country = "";
        int countryIndex = language.indexOf('-');
        if (countryIndex > -1)
        {
          country = language.substring(countryIndex + 1).trim();
          language = language.substring(0, countryIndex).trim();
        }
        locales.add(new Locale(language, country));
      }
    }
    return Collections.enumeration(locales);
  }

 */