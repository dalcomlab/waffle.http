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

import java.util.Deque;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Filterable<T extends Filter<T>> {

    /**
     * @return
     */
    Deque<T> getFilters();

    /**
     * Returns the first filter.
     *
     * @return
     */
    default T getFirstFilter() {
        Deque<T> filters = getFilters();
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        return filters.getFirst();
    }

    /**
     * Returns the last filter.
     *
     * @return
     */
    default T getLastFilter() {
        Deque<T> filters = getFilters();
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        return filters.getLast();
    }

    /**
     * Adds the filter.
     *
     * @param filter
     */
    default void addFilter(T filter) {
        addFilterLast(filter);
    }

    /**
     * Adds the filter.
     *
     * @param filters
     */
    default void addFilter(T... filters) {
        for (T filter : filters) {
            addFilter(filter);
        }
    }


    /**
     * Adds the filters at the front.
     *
     * @param filter
     */
    default void addFilterFirst(T filter) {
        Deque<T> filters = getFilters();
        if (filters == null || filter == null) {
            return;
        }
        T first = filters.peekFirst();
        if (first != null) {
            filter.next(first);
        }
        filters.addFirst(filter);
    }

    /**
     * Adds filters at the end.
     *
     * @param filter
     */
    default void addFilterLast(T filter) {
        Deque<T> filters = getFilters();
        if (filters == null || filter == null) {
            return;
        }

        T last = filters.peekLast();
        if (last != null) {
            last.next(filter);
        }
        filters.addLast(filter);
    }

    /**
     * Removes the given filter.
     *
     * @param filter
     */
    default void removeFilter(T filter) {
        Deque<T> filters = getFilters();
        if (filters == null || filter == null) {
            return;
        }
        filters.remove(filter);
    }

    /**
     * Removes the all filters.
     */
    default void removeAllFilters() {
        Deque<T> filters = getFilters();
        if (filters == null) {
            return;
        }
        filters.clear();
    }
}