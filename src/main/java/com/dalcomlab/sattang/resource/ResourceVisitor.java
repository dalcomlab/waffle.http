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
package com.dalcomlab.sattang.resource;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ResourceVisitor {

    /**
     * Visits a file of the resource.
     *
     * @param resource
     * @return
     */
    default  boolean visitFile(Resource resource) {
        return true;
    }

    /**
     * Visits a directory of the resource. This method should return false
     * if this visitor is not interested in visiting this directory.
     *
     * @param resource
     * @return
     */
    default boolean visitDirectory(Resource resource) {
        return true;
    }

    /**
     * Determines that this visitor should visit to resources more.
     *
     * @return
     */
    default boolean moreVisit() {
        return true;
    }
}
