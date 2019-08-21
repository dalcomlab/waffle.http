package com.dalcomlab.sattang.protocol.http.decoder;

import com.dalcomlab.sattang.protocol.DefaultHttpRequest;
import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpTooLongException;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;


public class HttpRequestDecoderTest {
    @Test
    public void testBasic() throws Exception {
        String httpRequest = "GET /test/test.jsp?A=A&B=B&C=C&D=D HTTP/1.1\r\n";
        httpRequest += "Content-Type:plain/text\r\n";
        httpRequest += "Content-Length:1000\r\n";
        httpRequest += "\r\n";

        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpRequest request = new DefaultHttpRequest(null);
        decoder.listen(request);
        decoder.decode(wrap(httpRequest.getBytes()));
        decoder.close();

        assertEquals(request.getMethod().name(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A"), "A");
        assertEquals(request.getParameter("B"), "B");
        assertEquals(request.getParameter("C"), "C");
        assertEquals(request.getParameter("D"), "D");
        assertEquals(request.getProtocol().name(), "HTTP/1.1");
        assertEquals(request.getHeader("Content-Type"), "plain/text");
        assertEquals(request.getHeader("Content-Length"), "1000");

    }

    @Test
    public void testBasic_SimulateAsync() throws Exception {
        String httpRequest = "GET /test/test.jsp?A=A&B=B&C=C&D=D HTTP/1.1\r\n";
        httpRequest += "Content-Type:plain/text\r\n";
        httpRequest += "Content-Length:1000\r\n";
        httpRequest += "\r\n";

        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpRequest request = new DefaultHttpRequest(null);
        decoder.listen(request);
        for (int i = 0; i < httpRequest.length(); i++) {
            decoder.decode(wrap(new byte[]{(byte) httpRequest.charAt(i)}));
        }
        decoder.close();

        assertEquals(request.getMethod().name(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A"), "A");
        assertEquals(request.getParameter("B"), "B");
        assertEquals(request.getParameter("C"), "C");
        assertEquals(request.getParameter("D"), "D");
        assertEquals(request.getProtocol().name(), "HTTP/1.1");
        assertEquals(request.getHeader("Content-Type"), "plain/text");
        assertEquals(request.getHeader("Content-Length"), "1000");

    }

    @Test
    public void testException_RequestLineBytes_Exceed_Limit() throws Exception {
        String httpRequestLine = "GET /test/test.jsp?A=A&B=B&C=C&D=D HTTP/1.1\r\n";
        String httpRequest = "";
        httpRequest += httpRequestLine;
        httpRequest += "Content-Type:plain/text\r\n";
        httpRequest += "Content-Length:1000\r\n";
        httpRequest += "\r\n";

        int maxRequestLineBytes = httpRequestLine.length() - 1; //
        int maxHeaderBytes = -1; // unlimited
        int maxHeaderCount = -1; // unlimited
        boolean tooLongException = false;
        HttpRequestDecoder decoder = new HttpRequestDecoder(maxRequestLineBytes, maxHeaderBytes, maxHeaderCount);
        HttpRequest request = new DefaultHttpRequest(null);
        try {
            decoder.listen(request);
            decoder.decode(wrap(httpRequest.getBytes()));
        } catch(HttpTooLongException e) {
            tooLongException = true;
        } finally {
            decoder.close();
        }
        assertEquals(tooLongException, true);
    }


    @Test
    public void testException_RequestHeaderBytes_Exceed_Limit() throws Exception {
        String httRequestLine = "GET /test/test.jsp?A=A&B=B&C=C&D=D HTTP/1.1\r\n";
        String httRequestHeader = "";

        httRequestHeader += "Content-Type:plain/text\r\n";
        httRequestHeader += "Content-Length:1000\r\n";

        String httpRequest = httRequestLine + httRequestHeader;
        httpRequest += "\r\n";

        int maxRequestLineBytes = - 1; // unlimited
        int maxHeaderBytes = httRequestHeader.length() - 1;
        int maxHeaderCount = -1; // unlimited
        boolean tooLongException = false;
        HttpRequestDecoder decoder = new HttpRequestDecoder(maxRequestLineBytes, maxHeaderBytes, maxHeaderCount);
        HttpRequest request = new DefaultHttpRequest(null);
        try {
            decoder.listen(request);
            decoder.decode(wrap(httpRequest.getBytes()));
            decoder.close();
        } catch(HttpTooLongException e) {
            tooLongException = true;
        } finally {

        }
        assertEquals(tooLongException, true);
    }


    @Test
    public void testException_RequestHeaderCount_Exceed_Limit() throws Exception {
        String httpRequest = "";
        httpRequest += "GET /test/test.jsp?A=A&B=B&C=C&D=D HTTP/1.1\r\n";
        httpRequest += "Content-Type:plain/text\r\n";
        httpRequest += "Content-Length:1000\r\n";
        httpRequest += "Cache-Control:max-age=0\r\n";
        httpRequest += "Accept-Encoding:gzip, deflate, b\r\n";
        httpRequest += "\r\n";

        int maxRequestLineBytes = - 1; // unlimited
        int maxHeaderBytes = -1; // unlimited
        int maxHeaderCount = 3;
        boolean tooLongException = false;
        HttpRequestDecoder decoder = new HttpRequestDecoder(maxRequestLineBytes, maxHeaderBytes, maxHeaderCount);
        HttpRequest request = new DefaultHttpRequest(null);
        try {
            decoder.listen(request);
            decoder.decode(wrap(httpRequest.getBytes()));
            decoder.close();
        } catch(HttpTooLongException e) {
            tooLongException = true;
        }
        assertEquals(tooLongException, true);
    }




    public ByteBuffer wrap(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }

}
