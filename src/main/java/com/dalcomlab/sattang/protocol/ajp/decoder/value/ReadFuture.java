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
package com.dalcomlab.sattang.protocol.ajp.decoder.value;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class ReadFuture<V> {
    protected boolean done = false;
    protected int state = 0;

    /**
     * Returns {@code true} if this value is available and calls consumer.
     *
     * @param consumer
     * @return {@code true} if this value is available
     */
    public boolean isDone(Consumer<V> consumer) {
        boolean old = done;
        if (done) {
            if (consumer != null) {
                consumer.accept(get());
            }
            done = false;
            reset();
        }
        return old;
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    public abstract V get();

    /**
     *
     * @param buffer
     * @throws Exception
     */
    public abstract void read(ByteBuffer buffer) throws Exception;

    /**
     *
     */
    public abstract void reset();
}
