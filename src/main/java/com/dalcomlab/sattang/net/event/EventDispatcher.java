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
package com.dalcomlab.sattang.net.event;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface EventDispatcher {

    /**
     * Starts the event dispatcher.
     */
    void start();

    /**
     * Registers a {@link EventExecutor} in this event dispatcher.
     * <p>
     * When the event of interest of the {@link EventExecutor}  occurs, this dispatcher
     * calls the {@link EventExecutor#execute} method of the given EventExecutor.
     *
     * @param event
     * @return
     */
    EventFuture register(EventExecutor event);

    /**
     * Stops the event dispatcher.
     * <p>
     * If there are registered {@link EventExecutor} events, this dispatcher calls
     * the {@link EventExecutor#cancel} method of the EventExecutor.
     */
    void stop();
}
