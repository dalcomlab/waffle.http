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

import java.util.concurrent.TimeoutException;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class Timeout {
    private long start = 0;
    private final long duration;

    /**
     * @param duration
     */
    public Timeout(long duration) {
        this.duration = duration;
    }

    /**
     * @return
     */
    public long duration() {
        return this.duration;
    }

    /**
     *
     */
    public void start() {
        this.start = System.currentTimeMillis();
    }

    /**
     *
     */
    public void reset() {
        this.start = System.currentTimeMillis();
    }

    /**
     * @throws TimeoutException
     */
    public void elapse() throws TimeoutException {
        if ((System.currentTimeMillis() - start) >= duration) {
            throw new TimeoutException();
        }
    }
}
