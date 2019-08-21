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
package com.dalcomlab.sattang.net.io.read;

import com.dalcomlab.sattang.net.Filter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ReadFilter extends Filter<ReadFilter> {

    /**
     * Reads the some data into the given buffer.
     * This method will be called in the {@link ReadChannel#read} method.
     * <p>
     * If there is an next filter and it is necessary, call the {@linke #read}
     * method of the next filter.
     *
     * @param channel
     * @param dst
     * @return
     * @throws IOException
     */
    default int read(ReadChannel channel, ByteBuffer dst) throws IOException {
        if (next() != null) {
            return next().read(channel, dst);
        }
        return 0;
    }
}
