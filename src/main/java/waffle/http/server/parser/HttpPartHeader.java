/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server.parser;

import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpPartHeader extends HttpHeader {

    private final static String MULTIPART = "multipart/";

    /**
     * @param headers
     */
    public HttpPartHeader(final Map<String, String> headers) {
        super(headers);
    }

    /**
     * @return
     */
    public boolean isMultipart() {
        String contentType = getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith(MULTIPART);
    }

    /**
     * Returns the name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; name="file"
     * </pre>
     *
     * @return
     */
    public String getName() {
        return getParameter(CONTENT_DISPOSITION, "name");
    }

    /**
     * Returns the file name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; filename="test.txt"
     * </pre>
     *
     * @return
     */
    public String getFileName() {
        return getParameter(CONTENT_DISPOSITION, "filename");
    }
}
