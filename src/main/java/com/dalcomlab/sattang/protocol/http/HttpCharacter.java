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

import java.util.BitSet;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public final class HttpCharacter {

    private static final BitSet ALLOWED_HEAD_CHARACTER = new BitSet();
    private static final BitSet ALLOWED_PATH_CHARACTER = new BitSet();
    private static final BitSet ALLOWED_QUERY_CHARACTER = new BitSet();

    static {
        //┌───────┬────────┐
        //│       │ allow  │
        //├───────┼────────┤
        //│ space │    X   │
        //│ !     │    O   │
        //│ "     │    X   │
        //│ #     │    O   │
        //│ $     │    O   │
        //│ %     │    O   │
        //│ &     │    O   │
        //│ '     │    O   │
        //│ (     │    X   │
        //│ )     │    X   │
        //│ *     │    O   │
        //│ +     │    O   │
        //│ ,     │    X   │
        //│ -     │    O   │
        //│ .     │    O   │
        //│ /     │    X   │
        //│ :     │    X   │
        //│ ;     │    X   │
        //│ <     │    X   │
        //│ =     │    X   │
        //│ >     │    X   │
        //│ ?     │    X   │
        //│ @     │    X   │
        //│ [     │    X   │
        //│ \     │    X   │
        //│ ]     │    X   │
        //│ ^     │    O   │
        //│ _     │    O   │
        //│ `     │    O   │
        //│ {     │    X   │
        //│ |     │    O   │
        //│ }     │    X   │
        //│ ~     │    O   │
        //└───────┴────────┘
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'*+-.^_`|~".chars().forEach(c -> {
            ALLOWED_HEAD_CHARACTER.set(c);
        });


        //┌───────┬────────┐
        //│       │ allow  │
        //├───────┼────────┤
        //│ space │    X   │
        //│ !     │    O   │
        //│ "     │    X   │
        //│ #     │    X   │
        //│ $     │    O   │
        //│ %     │    O   │
        //│ &     │    O   │
        //│ '     │    O   │
        //│ (     │    O   │
        //│ )     │    O   │
        //│ *     │    O   │
        //│ +     │    O   │
        //│ ,     │    O   │
        //│ -     │    O   │
        //│ .     │    O   │
        //│ /     │    O   │
        //│ :     │    O   │
        //│ ;     │    O   │
        //│ <     │    X   │
        //│ =     │    O   │
        //│ >     │    X   │
        //│ ?     │    O   │
        //│ @     │    O   │
        //│ [     │    O   │
        //│ \     │    X   │
        //│ ]     │    O   │
        //│ ^     │    X   │
        //│ _     │    O   │
        //│ `     │    X   │
        //│ {     │    X   │
        //│ |     │    X   │
        //│ }     │    X   │
        //│ ~     │    O   │
        //└───────┴────────┘
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$%&'()*+,-./:;=?@[]_~".chars().forEach(c -> {
            ALLOWED_PATH_CHARACTER.set(c);
        });

        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*,;=".chars().forEach(c -> {
            ALLOWED_QUERY_CHARACTER.set(c);
        });
    }


    /**
     * @param c
     * @return
     */
    public static boolean isAllowedHeadCharacter(char c) {
        return ALLOWED_HEAD_CHARACTER.get(c & 0xFF);
    }


    /**
     * @param c
     * @return
     */
    public static boolean isAllowedPathCharacter(char c) {
        return ALLOWED_PATH_CHARACTER.get(c & 0xFF);
    }


    /**
     * @param c
     * @return
     */
    public static boolean isAllowedQueryCharacter(char c) {
        return ALLOWED_QUERY_CHARACTER.get(c & 0xFF);
    }

}
