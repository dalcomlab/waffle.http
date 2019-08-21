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
package com.dalcomlab.sattang.net.socket;

import com.dalcomlab.sattang.concurrent.CompletionHandler;
import com.dalcomlab.sattang.net.ReadQueue;
import com.dalcomlab.sattang.net.event.EventDispatcher;
import com.dalcomlab.sattang.net.event.EventExecutor;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class SocketReadQueue implements ReadQueue {
    private final EventDispatcher dispatcher;
    private final SocketChannel channel;
    //private final Queue<IORecord> queue = new ConcurrentLinkedQueue<>();

    /**
     * @param channel
     * @param dispatcher
     */
    public SocketReadQueue(SocketChannel channel, EventDispatcher dispatcher) {
        this.channel = channel;
        this.dispatcher = dispatcher;
    }

    /**
     * @param buffer
     * @param handler
     */
    @Override
    public void read(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) {
      /*  if (handler == null) {
            handler = CompletionHandler.INSTANCE;
        }

        // workaround for an empty buffer.
        if (!buffer.hasRemaining()) {
                handler.completed(buffer);
            return;
        }

        if (queue.isEmpty()) {
            try {
                channel.read(buffer);
            } catch (Exception e) {
                handler.failed(e);
            }

            if (!buffer.hasRemaining()) {
                handler.completed(buffer);
                return;
            } else {
                handler.update(buffer);
            }

        }

        queue.offer(new IORecord(buffer, handler));

        dispatcher.register(new EventExecutor<SocketChannel>() {
            @Override
            public SocketChannel channel() {
                return channel;
            }

            public SocketEvent event() {
                return SocketEvent.READ;
            }

            @Override
            public void execute(SocketChannel channel, EventDispatcher dispatcher) {
                process(channel, dispatcher, this);
            }
        });
       */
    }

    /**
     *
     */
    @Override
    public void end() {
        /*
        queue.stream().forEach(record -> {
            record.end();
        });
        queue.clear();

         */
    }

    /**
     *
     */
    @Override
    public void close() {
        /*
        queue.stream().forEach(record -> {
            record.end();
        });
        queue.clear();
         */
    }

    /**
     * @return
     */
    @Override
    public boolean canRead() {
        return false;
    }

    /**
     * @param channel
     * @param dispatcher
     */
    private void process(SocketChannel channel, EventDispatcher dispatcher, EventExecutor<SocketChannel> event) {
        /*
        IORecord record = queue.peek();
        if (record != null) {
            ByteBuffer buffer = record.getBuffer();
            try {
                channel.read(buffer);
                if (buffer.hasRemaining()) {
                    record.update();
                } else {
                    record.complete();
                    queue.poll();
                }

            } catch (Exception e) {
                queue.poll();
            } finally {
                if (!queue.isEmpty()) {
                    dispatcher.register(event);
                }
            }
        }
         */
    }

}
