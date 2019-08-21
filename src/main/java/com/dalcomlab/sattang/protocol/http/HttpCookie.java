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

import java.util.Optional;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface HttpCookie {

    String PATH = "Path";
    String EXPIRES = "Expires";
    String MAX_AGE = "Max-Age";
    String DOMAIN = "Domain";
    String SECURE = "Secure";
    String HTTPONLY = "HTTPOnly";


    /**
     * Returns the name of this.
     *
     * @return The name of this cookie
     */
    Optional<String> getName();

    /**
     * Returns the value of this cookie.
     *
     * @return The value of this cookie
     */
    Optional<String> getValue();

    /**
     * Sets the value of this cookie.
     *
     * @param value The value to set
     */
    void setValue(String value);

    /**
     * Returns the domain of this cookie.
     *
     * @return The domain of this cookie
     */
    Optional<String> getDomain();

    /**
     * Sets the domain of this cookie.
     *
     * @param domain The domain to use
     */
    void setDomain(String domain);

    /**
     * Returns the path of this cookie.
     *
     * @return The the cookie's path
     */
    Optional<String> getPath();

    /**
     * Sets the path of this cookie.
     *
     * @param path The path to use for this cookie
     */
    void setPath(String path);

    /**
     * Returns the maximum age of this cookie in seconds
     *
     * @return The maximum age of this cookie
     */
    Optional<Long> getMaxAge();

    /**
     * Sets the maximum age of this cookie in seconds.
     *
     * @param maxAge The maximum age of this cookie in seconds
     */
    void setMaxAge(long maxAge);

    /**
     * Checks to see if this cookie is secure
     *
     * @return True if this cookie is secure, otherwise false
     */
    Optional<Boolean> isSecure();

    /**
     * Sets the security of this cookie
     *
     * @param secure True if this cookie is to be secure, otherwise false
     */
    void setSecure(boolean secure);

    /**
     * Checks to see if this cookie can only be accessed via HTTP.
     * If this returns true, the the cookie cannot be accessed through
     * client side script - But only if the browser supports it.
     * For more information, please look <a href="http://www.owasp.org/index.php/HTTPOnly">here</a>
     *
     * @return True if this cookie is HTTP-only or false if it isn't
     */
    Optional<Boolean> isHttpOnly();

    /**
     * Determines if this Cookie is HTTP only.
     * If set to true, this cookie cannot be accessed by a client
     * side script. However, this works only if the browser supports it.
     * For for information, please look
     * <a href="http://www.owasp.org/index.php/HTTPOnly">here</a>.
     *
     * @param httpOnly True if the the cookie is HTTP only, otherwise false.
     */
    void setHttpOnly(boolean httpOnly);
}
