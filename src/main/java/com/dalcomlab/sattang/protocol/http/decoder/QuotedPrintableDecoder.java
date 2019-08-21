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

import java.io.ByteArrayOutputStream;

/**
 * http://www.webutils.pl/index.php?idx=qp
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class QuotedPrintableDecoder {

    /**
     * @param text
     * @return
     */
    public static byte[] decode(byte[] text) {
        if (text == null || text.length == 0) {
            return null;
        }
        int length = text.length;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int i = 0; i < length; i++) {
            byte c = text[i];
            if (c == '_') {
                output.write(' ');
            } else if (c == '=') {
                if ((length - i) < 2) {
                    // error
                    break;
                }

                byte b1 = text[++i];
                byte b2 = text[++i];

                if (b1 == '\r' && b1 == '\n') {
                    continue;
                }

                if (isHexDigit(b1) && isHexDigit(b2)) {
                    int c1 = hexToBin(b1);
                    int c2 = hexToBin(b2);
                    output.write(c1 * 16 + c2);
                    continue;
                }
                // error
            } else {
                output.write(c);
            }
        }
        return output.toByteArray();
    }

    private static boolean isHexDigit(byte b) {
        return ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x46));
    }

    private static int hexToBin(byte b) {
        return Character.digit((char) b, 16);
    }
}
