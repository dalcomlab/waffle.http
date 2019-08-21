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
package com.dalcomlab.sattang.protocol.http;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface HttpCompatible {

    /**
     * HTTP/0.9
     */
    HttpCompatible HTTP09 = new HttpCompatible() {
        @Override
        public boolean isSupportUpgrade() {
            return false;
        }

        @Override
        public boolean isSupportKeepAlive() {
            return false;
        }

        @Override
        public boolean isSupportChunkedEncoding() {
            return false;
        }
    };

    /**
     * HTTP/1.0
     */
    HttpCompatible HTTP10 = new HttpCompatible() {
        @Override
        public boolean isSupportUpgrade() {
            return false;
        }

        @Override
        public boolean isSupportKeepAlive() {
            return true;
        }

        @Override
        public boolean isSupportChunkedEncoding() {
            return true;
        }
    };

    /**
     * HTTP/1.1
     */
    HttpCompatible HTTP11 = new HttpCompatible() {
        @Override
        public boolean isSupportUpgrade() {
            return true;
        }

        @Override
        public boolean isSupportKeepAlive() {
            return true;
        }

        @Override
        public boolean isSupportChunkedEncoding() {
            return true;
        }
    };


    /**
     * Determines that the HTTP protocol supports the upgrade protocol.
     *
     * @return
     */
    boolean isSupportUpgrade();

    /**
     * Determines that the HTTP protocol supports the connection keep alive.
     *
     * @return
     */
    boolean isSupportKeepAlive();

    /**
     * Determines that the HTTP protocol supports the chunked encoding.
     *
     * @return
     */
    boolean isSupportChunkedEncoding();

}
