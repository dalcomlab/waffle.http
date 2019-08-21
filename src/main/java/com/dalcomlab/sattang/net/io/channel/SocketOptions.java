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

package com.dalcomlab.sattang.net.io.channel;

import java.net.NetworkInterface;
import java.util.Optional;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public final class SocketOptions {

    private Boolean soBroadcast = null;
    private Boolean soKeepalive = null;
    private Integer soSendBufferSize = null;
    private Integer soReceiveBufferSize = null;
    private Boolean soReuseaddr = null;
    private Integer soLinger = null;
    private Integer ipTos = null;
    private NetworkInterface ipMulticastIf = null;
    private Integer ipMulticastTtl = null;
    private Boolean ipMulticastLoop = null;
    private Boolean tcpNoDelay = null;


    /**
     * @return
     */
    public Optional<Boolean> getBroadcast() {
        return Optional.ofNullable(soBroadcast);
    }

    /**
     * @param soBroadcast
     */
    public void setBroadcast(Boolean soBroadcast) {
        this.soBroadcast = soBroadcast;
    }

    /**
     * @return
     */
    public Optional<Boolean> getKeepAlive() {
        return Optional.ofNullable(soKeepalive);
    }

    /**
     * @param soKeepalive
     */
    public void setKeepAlive(Boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    /**
     * @return
     */
    public Optional<Integer> getSendBufferSize() {
        return Optional.ofNullable(soSendBufferSize);
    }

    /**
     * @param soSendBufferSize
     */
    public void setSendBufferSize(Integer soSendBufferSize) {
        this.soSendBufferSize = soSendBufferSize;
    }

    /**
     * @return
     */
    public Optional<Integer> getReceiveBufferSize() {
        return Optional.ofNullable(soReceiveBufferSize);
    }

    /**
     * @param soReceiveBufferSize
     */
    public void setReceiveBufferSize(Integer soReceiveBufferSize) {
        this.soReceiveBufferSize = soReceiveBufferSize;
    }

    /**
     * @return
     */
    public Optional<Boolean> getReuseAddr() {
        return Optional.ofNullable(soReuseaddr);
    }

    /**
     * @param soReuseaddr
     */
    public void setReuseAddr(Boolean soReuseaddr) {
        this.soReuseaddr = soReuseaddr;
    }

    /**
     * @return
     */
    public Optional<Integer> getSolLinger() {
        return Optional.ofNullable(soLinger);
    }

    /**
     * @param soLinger
     */
    public void setSoLinger(Integer soLinger) {
        this.soLinger = soLinger;
    }

    /**
     * @return
     */
    public Optional<Integer> getIpTos() {
        return Optional.ofNullable(ipTos);
    }

    /**
     * @param ipTos
     */
    public void setIpTos(Integer ipTos) {
        this.ipTos = ipTos;
    }

    /**
     * @return
     */
    public Optional<NetworkInterface> getIpMulticastIf() {
        return Optional.ofNullable(ipMulticastIf);
    }

    /**
     * @param ipMulticastIf
     */
    public void setIpMulticastIf(NetworkInterface ipMulticastIf) {
        this.ipMulticastIf = ipMulticastIf;
    }

    /**
     * @return
     */
    public Optional<Integer> getIpMulticastTtl() {
        return Optional.ofNullable(ipMulticastTtl);
    }

    /**
     * @param ipMulticastTtl
     */
    public void setIpMulticastTtl(Integer ipMulticastTtl) {
        this.ipMulticastTtl = ipMulticastTtl;
    }

    /**
     * @return
     */
    public Optional<Boolean> getIpMulticastLoop() {
        return Optional.ofNullable(ipMulticastLoop);
    }

    /**
     * @param ipMulticastLoop
     */
    public void setIpMulticastTtl(Boolean ipMulticastLoop) {
        this.ipMulticastLoop = ipMulticastLoop;
    }

    /**
     * @return
     */
    public Optional<Boolean> getTcpNodelay() {
        return Optional.ofNullable(tcpNoDelay);
    }

    /**
     * @param tcpNoDelay
     */
    public void setTcpNodelay(Boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

}
