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
package com.dalcomlab.sattang.server;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ServerListener {
    /**
     * This event is fired when the {@link Server#start} method is called.
     */
    default void onStart() {
    }

    /**
     * This event is fired when the {@link Server#stop} method is called.
     */
    default void onStop() {
    }
}
