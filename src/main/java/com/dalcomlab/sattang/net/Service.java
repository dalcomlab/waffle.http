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
package com.dalcomlab.sattang.net;

import java.util.concurrent.TimeUnit;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Service<T> {

    /**
     * Returns the state of this service.
     *
     * @return
     */
    State getState();

    /**
     * @return
     */
    default boolean isStarted() {
        if (getState() == null) {
            return false;
        }
        State state = getState();
        synchronized (state) {
            if (state == null) {
                return false;
            }
            if (state == State.STOPPING || state == State.STOPPED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the service.
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * Stops the service and close all resource.
     *
     * @return
     */
    void stop();

    /**
     * Stops the service and close all resource.
     *
     * @param timeout
     * @param timeUnit
     * @return
     */
    void stop(long timeout, TimeUnit timeUnit);

    /**
     *
     */
    enum State {STARTING, STARTED, PAUSING, PAUSED, STOPPING, STOPPED}
}
