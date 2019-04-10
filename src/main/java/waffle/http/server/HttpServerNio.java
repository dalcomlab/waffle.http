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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpServerNio extends AbstractHttpServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private SelectionKeyHandler acceptHandler;
    private SelectionKeyHandler readHandler;

    public HttpServerNio(InetSocketAddress address) {
        super(address);
    }

    /**
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        super.init();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = this.serverSocketChannel.socket();
        serverSocket.bind(inetSocketAddress, 100);
        SelectorProvider selectorProvider = SelectorProvider.provider();
        selector = selectorProvider.openSelector();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        acceptHandler = new AcceptSelectionKeyHandler(selector);
        readHandler = new ReadSelectionKeyHandler(selector);
    }

    /**
     *
     */
    @Override
    public final void start() {
        try {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                this.selector.select(1000);
                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                handleSelectionKeys(selectedKeys);
            }
        } catch (IOException ex) {
            stop();
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param selectionKeys
     * @throws IOException
     */
    private void handleSelectionKeys(Set<SelectionKey> selectionKeys) throws IOException {

        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();
            // remove as to not process again.
            try {
                if (!selectionKey.isValid()) {
                    continue;
                }
                if (selectionKey.isAcceptable()) {
                    acceptHandler.execute(selectionKey);
                } else if (selectionKey.isReadable()) {
                    readHandler.execute(selectionKey);
                } else if (selectionKey.isWritable()) {
                    // to do something
                }

            } catch (Exception e) {
                e.printStackTrace();
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                socketChannel.close();
            }
        }
    }

    /**
     * Shutdown this server, preventing it from handling any more requests.
     */
    @Override
    public final void stop() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException ex) {
        }
    }
}
