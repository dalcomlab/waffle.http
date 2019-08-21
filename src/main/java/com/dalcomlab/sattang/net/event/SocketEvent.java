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

import java.nio.channels.SelectionKey;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public enum SocketEvent {
    NONE(0),
    READ(SelectionKey.OP_READ),
    WRITE(SelectionKey.OP_WRITE),
    ACCEPT(SelectionKey.OP_ACCEPT),
    CONNECT(SelectionKey.OP_CONNECT);

    private final int code;

    /**
     * @param code
     */
    SocketEvent(int code) {
        this.code = code;
    }

    /**
     * @param code
     * @return
     */
    public static SocketEvent valueOf(int code) {
        if (code == SelectionKey.OP_READ) {
            return SocketEvent.READ;
        }

        if (code == SelectionKey.OP_WRITE) {
            return SocketEvent.WRITE;
        }

        if (code == SelectionKey.OP_ACCEPT) {
            return SocketEvent.ACCEPT;
        }

        if (code == SelectionKey.OP_CONNECT) {
            return SocketEvent.CONNECT;
        }

        return SocketEvent.NONE;
    }

    /**
     * @return
     */
    public boolean isRead() {
        return this == SocketEvent.READ;
    }

    /**
     * @return
     */
    public boolean isWrite() {
        return this == SocketEvent.WRITE;
    }

    /**
     * @return
     */
    public boolean isAccept() {
        return this == SocketEvent.ACCEPT;
    }

    /**
     * @return
     */
    public boolean isConnect() {
        return this == SocketEvent.CONNECT;
    }

}
