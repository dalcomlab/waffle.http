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
package com.dalcomlab.sattang.protocol.http.filters;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.protocol.http.ResponseFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseGZipFilter extends ResponseFilter {
    private GZIPOutputStream gzipOutputStream = null;
    private WriteChannel channel = null;

    /**
     *
     */
    public ResponseGZipFilter() {

    }


    /**
     * Writes the given buffer to this filters.
     *
     * @param channel
     * @param source
     * @return
     * @throws IOException
     */
    @Override
    public int write(WriteChannel channel, ByteBuffer source) throws IOException {
        if (next() == null) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (source.remaining() == 0 || !source.hasArray()) {
            return 0;
        }

        if (gzipOutputStream == null) {
            gzipOutputStream = new GZIPOutputStream(new DelegateOutputStream(), true);
        }

        this.channel = channel;
        int length = source.remaining();
        gzipOutputStream.write(source.array(), source.arrayOffset() + source.position(), length);
        source.position(source.position() + length);
        return length;
    }


    /**
     * Flushes the buffer.
     *
     * @throws IOException
     */
    @Override
    public void flush(WriteChannel channel) throws IOException {
        if (gzipOutputStream != null) {
            try {
                gzipOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (next() != null) {
            next().flush(channel);
        }
    }

    /**
     *
     */
    protected class DelegateOutputStream extends OutputStream {
        protected final ByteBuffer outputChunk = ByteBuffer.allocate(1);

        @Override
        public void write(int b) throws IOException {
            outputChunk.put(0, (byte) (b & 0xff));
            if (next() != null) {
                next().write(channel, outputChunk);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (next() != null) {
                next().write(channel, ByteBuffer.wrap(b, off, len));
            }
        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }
    }
}
