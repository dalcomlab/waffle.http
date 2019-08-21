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
package com.dalcomlab.sattang.resource.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceStreamFile implements ResourceStream {

    protected File file = null;

    /**
     *
     * @param file
     */
    public ResourceStreamFile(final File file) {
        this.file = file;
    }

    /**
     * Gets the length of this stream.
     *
     * @return {@link File#length()}.
     */
    @Override
    public long getContentLength() {
      if (this.file == null) {
          return 0;
      }
      return this.file.length();
    }

    /**
     * Gets the input stream of this stream.
     *
     * @return the input stream object
     */
    @Override
    public InputStream getInputStream() {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }
}
