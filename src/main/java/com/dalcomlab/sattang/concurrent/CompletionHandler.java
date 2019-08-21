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
package com.dalcomlab.sattang.concurrent;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface CompletionHandler<T> {

    CompletionHandler INSTANCE = new CompletionHandler() {
        @Override
        public void failed(Throwable throwable) {
        }

        @Override
        public void completed(Object result) {

        }

        @Override
        public void update(Object result) {
        }
    };

    /**
     * The operation has failed.
     *
     * @param throwable error, which occurred during operation execution.
     */
    default void failed(Throwable throwable) {

    }

    /**
     * The operation has completed.
     *
     * @param result the operation result.
     */
    default void completed(T result) {

    }

    /**
     * The this method may be called, when there is some progress in
     * operation execution, but it is still not completed.
     *
     * @param result
     */
    default void update(T result) {

    }
}
