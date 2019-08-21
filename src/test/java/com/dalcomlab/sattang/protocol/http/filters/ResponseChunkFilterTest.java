package com.dalcomlab.sattang.protocol.http.filters;

import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.WriteFilter;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ResponseChunkFilterTest {

    public ResponseChunkFilter createResponseChunkFilter(WriteFilter next) {
        ResponseChunkFilter chunkFilter = new ResponseChunkFilter();
        chunkFilter.next(next);
        return chunkFilter;
    }

    @Test
    public void testSimple1() throws Exception {
        MockResultFilter result = new MockResultFilter();
        ResponseChunkFilter chunkFilter = createResponseChunkFilter(result);

        chunkFilter.write(null, ByteBuffer.wrap("a".getBytes()));

        assertEquals(result.toString(), "1\r\na\r\n");
    }


    @Test
    public void testSimple11() throws Exception {
        MockResultFilter result = new MockResultFilter();
        ResponseChunkFilter chunkFilter = createResponseChunkFilter(result);

        for (int i = 0; i < 3; i++) {
            chunkFilter.write(null, ByteBuffer.wrap("a".getBytes()));
        }

        assertEquals(result.toString(), "1\r\na\r\n1\r\na\r\n1\r\na\r\n");
    }


    @Test
    public void testSimple2() throws Exception {
        MockResultFilter result = new MockResultFilter();
        ResponseChunkFilter chunkFilter = createResponseChunkFilter(result);

        chunkFilter.write(null, ByteBuffer.wrap("123456789".getBytes()));

        assertEquals(result.toString(), "9\r\n123456789\r\n");
    }


    @Test
    public void testSimple3() throws Exception {
        MockResultFilter result = new MockResultFilter();
        ResponseChunkFilter chunkFilter = createResponseChunkFilter(result);

        chunkFilter.write(null, ByteBuffer.wrap("1234567890".getBytes()));

        assertEquals(result.toString(), "a\r\n1234567890\r\n");
    }

    /**
     *
     */
    private class MockResultFilter implements WriteFilter {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        @Override
        public int write(WriteChannel channel, ByteBuffer source) throws IOException {
            while (source.hasRemaining()) {
                result.write(source.get());
            }
            return 0;
        }

        public String toString() {
            return new String(result.toByteArray());
        }
    }

}