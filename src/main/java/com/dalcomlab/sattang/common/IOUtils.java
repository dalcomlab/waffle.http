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
package com.dalcomlab.sattang.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class IOUtils {

    public static final int BUFFERSIZE = 64 * 1024;

    /**
     *
     * @param in
     * @param out
     * @param bytes
     * @throws IOException
     */
    public static void copy2(InputStream in, OutputStream out, long bytes) throws IOException {
        byte buffer[] = new byte[BUFFERSIZE];
        int len = BUFFERSIZE;

        if (bytes >= 0) {
            while (bytes > 0) {
                int max = bytes < BUFFERSIZE ? (int) bytes : BUFFERSIZE;
                len = in.read(buffer, 0, max);
                if (len == -1) {
                    break;
                }

                bytes -= len;
                out.write(buffer, 0, len);
            }
        } else {
            while (true) {
                len = in.read(buffer, 0, BUFFERSIZE);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
    }

    public static void copy(InputStream in, OutputStream out, long bytes) throws IOException {
        byte buffer[] = new byte[BUFFERSIZE];
        copyLarge(in, out, bytes, buffer);
    }

    public static long copyLarge(InputStream input, OutputStream output,
                                 long length, byte[] buffer) throws IOException {
        if (length == 0) {
            return 0;
        }
        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;
        if (length > 0 && length < bufferLength) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }
        return totalRead;
    }

    /**
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[BUFFERSIZE];
        int len;
        while (true) {
            len = in.read(buffer, 0, BUFFERSIZE);
            if (len < 0) {
                break;
            }
            out.write(buffer, 0, len);
        }
    }



    /**
     * Gets the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output);
            return output.toByteArray();
        }
    }
}
