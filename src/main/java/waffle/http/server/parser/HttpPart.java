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

import java.io.*;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpPart {

    private final HttpPartHeader header;
    private FileOutputStream output = null;
    private File file = null;

    /**
     * Creates new HttpPart object.
     *
     * @param header
     */
    public HttpPart(final HttpPartHeader header) {
        this.header = header;
        try {
            file = File.createTempFile("demo", ".waffle.tmp");
            this.output = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the content type in the header.
     * <pre>
     *     Content-Line: text/plain
     * </pre>
     *
     * @return
     */
    public String getContentType() {
        return header.getContentType();
    }

    /**
     * Returns the content disposition in the header.
     *
     * @return
     */
    public String getContentDisposition() {
        return header.getContentDisposition();
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
        return header.getName();
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
        return header.getFileName();
    }

    /**
     * Returns the names of the headers of this multipart.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this multipart.
     */
    public Set<String> getHeaderNames() {
        return header.getHeaderNames();
    }

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return header.getHeader(name);
    }

    /**
     * Returns the multipart header {@link HttpPartHeader}
     *
     * @return
     */
    public HttpPartHeader getHeader() {
        return header;
    }

    /**
     * @return
     */
    public OutputStream getOutputStream() {
        return output;
    }

    /**
     * @param path
     * @throws Exception
     */
    public void writeFile(String path) throws Exception {
        writeFile(new File(path));
    }

    /**
     * @throws Exception
     */
    public void writeFile(File dest) throws Exception {
        if (file != null) {
            if (!file.renameTo(dest)) {
                BufferedInputStream in = null;
                BufferedOutputStream out = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(file));
                    out = new BufferedOutputStream(new FileOutputStream(dest));
                    copy(in, out);
                } finally {
                    close(in);
                    close(out);
                }
            }
        } else {
            throw new Exception("Cannot write uploaded file to disk!");
        }
    }


    public long copy(final InputStream input, final OutputStream output) throws IOException {

        byte[] buffer = new byte[1024 * 8];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public void close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

}
