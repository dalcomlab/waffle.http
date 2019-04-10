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
package waffle.http.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ReadSelectionKeyHandler extends SelectionKeyHandler {
    /**
     * @param selector
     */
    public ReadSelectionKeyHandler(Selector selector) {
        super(selector);
    }

    /**
     * @param selectionKey
     * @throws IOException
     */
    public void execute(SelectionKey selectionKey) throws IOException {
        boolean readable = selectionKey.isReadable();
        if (!readable) {
            return;
        }

        HttpRequestHandler httpRequestHandler = (HttpRequestHandler) selectionKey.attachment();
        if (httpRequestHandler == null) {
            httpRequestHandler = new HttpRequestHandler();
            selectionKey.attach(httpRequestHandler);
        }

        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        try {
            httpRequestHandler.read(socketChannel);
        } catch (IOException e) {
            socketChannel.close();
            selectionKey.cancel();
        }
    }
}
