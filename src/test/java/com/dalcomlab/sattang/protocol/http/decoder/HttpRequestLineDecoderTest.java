package com.dalcomlab.sattang.protocol.http.decoder;

import com.dalcomlab.sattang.protocol.HttpBadRequestException;
import com.dalcomlab.sattang.protocol.HttpTooLongException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HttpRequestLineDecoderTest {

    @Test
    public void testRequestLine_Just_One_Space() throws Exception {
        String requestLine = "GET / HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Just_One_Space_SimulateAsync() throws Exception {
        String requestLine = "GET\t/\tHTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Multiple_Space() throws Exception {
        String requestLine = "GET    /    HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Multiple_Space_SimulateAsync() throws Exception {
        String requestLine = "GET    /    HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Just_One_Tab() throws Exception {
        String requestLine = "GET\t/\tHTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Just_One_Tab_SimulateAsync() throws Exception {
        String requestLine = "GET\t/\tHTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }
    //

    @Test
    public void testRequestLine_Multiple_Tab() throws Exception {
        String requestLine = "GET\t\t\t\t/\t\t\t\tHTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testRequestLine_Multiple_Tab_SimulateAsync() throws Exception {
        String requestLine = "GET\t\t\t\t/\t\t\t\tHTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }


    @Test
    public void testUri_WithoutQueryString() throws Exception {
        String requestLine = "GET /test/test.jsp HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 0);
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testUri_WithoutQueryString_SimulateAsync() throws Exception {
        String requestLine = "GET /test/test.jsp HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();
        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 0);
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    //
    @Test
    public void testUri_WithQueryString() throws Exception {
        String requestLine = "GET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 2);
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testUri_WithQueryString_SimulateAsync() throws Exception {
        String requestLine = "GET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 2);
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }


    @Test
    public void testMethod_StartWith_Space() throws Exception {
        String requestLine = "             GET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_StartWith_Space_SimulateAsync() throws Exception {
        String requestLine = "             GET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_StartWith_Tab() throws Exception {
        String requestLine = "\t\t\t\tGET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_StartWith_Tab_SimulateAsync() throws Exception {
        String requestLine = "\t\t\t\tGET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }


    @Test
    public void testMethod_StartWith_Crlf() throws Exception {
        String requestLine = "\r\n\r\n\r\nGET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_StartWith_Crlf_SimulateAsync() throws Exception {
        String requestLine = "\r\n\r\n\r\nGET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }


    @Test
    public void testMethod_EndWith_Space() throws Exception {
        String requestLine = "GET              /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_EndWith_Space_SimulateAsync() throws Exception {
        String requestLine = "GET              /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_EndWith_Tab() throws Exception {
        String requestLine = "GET\t\t\t\t/test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testMethod_EndWith_Tab_SimulateAsync() throws Exception {
        String requestLine = "GET\t\t\t\t/test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameter("A", 0), "A");
        assertEquals(request.getParameter("B", 0), "B");
        assertEquals(request.getProtocol(), "HTTP/1.1");
    }


    @Test
    public void testUri_Stop_QueryString_01() throws Exception {
        String requestLine = "GET /test/test.jsp? HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 0);
    }


    @Test
    public void testUri_Stop_QueryString_01_SimulateAsync() throws Exception {
        String requestLine = "GET /test/test.jsp? HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test/test.jsp");
        assertEquals(request.getParameterCount(), 0);
    }


    @Test
    public void testUri_Stop_QueryString_02() throws Exception {
        String requestLine = "GET /? HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getParameterCount(), 0);
    }


    @Test
    public void testUri_Stop_QueryString_02_SimulateAsync() throws Exception {
        String requestLine = "GET /? HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getParameterCount(), 0);
    }


    @Test
    public void testUri_Decode_01() throws Exception {
        String requestLine = "GET /t%20e%20s%20t/t%20e%20s%20t.jsp HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/t e s t/t e s t.jsp");
        assertEquals(request.getParameterCount(), 0);
    }

    @Test
    public void testUri_Decode_01_SimulateAsync() throws Exception {
        String requestLine = "GET /t%20e%20s%20t/t%20e%20s%20t.jsp HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/t e s t/t e s t.jsp");
        assertEquals(request.getParameterCount(), 0);
    }


    @Test
    public void testProtocol_Missing_Http09_01() throws Exception {
        String requestLine = "GET /\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/0.9");
    }

    @Test
    public void testProtocol_Missing_Http09_01_SimulateAsync() throws Exception {
        String requestLine = "GET /\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/");
        assertEquals(request.getProtocol(), "HTTP/0.9");
    }


    @Test
    public void testProtocol_Missing_Stop_QuestionMark_Http09() throws Exception {
        String requestLine = "GET /test.jsp?\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test.jsp");
        assertEquals(request.getProtocol(), "HTTP/0.9");
    }

    @Test
    public void testProtocol_Missing_Stop_QuestionMark_Http09_SimulateAsync() throws Exception {
        String requestLine = "GET /test.jsp?\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        for (int i = 0; i < requestLine.length(); i++) {
            decoder.decode(new byte[]{(byte) requestLine.charAt(i)});
        }
        decoder.close();

        assertEquals(request.getMethod(), "GET");
        assertEquals(request.getUri(), "/test.jsp");
        assertEquals(request.getProtocol(), "HTTP/0.9");
    }


    @Test(expected = HttpTooLongException.class)
    public void testMaxLineSize_Throw_HttpTooLongException() throws Exception {
        String requestLine = "GET /test/test.jsp HTTP/1.1\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder(requestLine.length() - 1); // !!
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
    }

    @Test(expected = HttpBadRequestException.class)
    public void testMissingUri_Throw_HttpBadRequestException_01() throws Exception {
        String requestLine = "GET\r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();
    }

    @Test(expected = HttpBadRequestException.class)
    public void testMissingUri_Throw_HttpBadRequestException_02() throws Exception {
        String requestLine = "GET   \t\t\t   \r\n";
        MockRequest request = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(request);
        decoder.decode(requestLine.getBytes());
        decoder.close();
    }


    @Test
    public void testReset() throws Exception {
        String requestLine = "GET /test/test.jsp?A=A&B=B HTTP/1.1\r\n";
        MockRequest requestA = new MockRequest();
        HttpRequestLineDecoder decoder = new HttpRequestLineDecoder();
        decoder.listen(requestA);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(requestA.getMethod(), "GET");
        assertEquals(requestA.getUri(), "/test/test.jsp");
        assertEquals(requestA.getParameterCount(), 2);
        assertEquals(requestA.getParameter("A", 0), "A");
        assertEquals(requestA.getParameter("B", 0), "B");
        assertEquals(requestA.getProtocol(), "HTTP/1.1");

        decoder.reset();

        requestLine = "POST /reset/reset.jsp?C=C&D=D HTTP/1.0\r\n";
        MockRequest requestB = new MockRequest();
        decoder.listen(requestB);
        decoder.decode(requestLine.getBytes());
        decoder.close();

        assertEquals(requestB.getMethod(), "POST");
        assertEquals(requestB.getUri(), "/reset/reset.jsp");
        assertEquals(requestB.getParameterCount(), 2);
        assertEquals(requestB.getParameter("C", 0), "C");
        assertEquals(requestB.getParameter("D", 0), "D");
        assertEquals(requestB.getProtocol(), "HTTP/1.0");
    }



    /**
     *
     */
    private class MockRequest implements HttpRequestLineDecoder.Listener {

        private String method;
        private String uri;
        private String protocol;
        private Map<String, List<String>> parameters = new HashMap<>();

        @Override
        public void addParameter(String name, String value) {
            List<String> values = parameters.get(name);
            if (values == null) {
                values = new ArrayList<>();
                parameters.put(name, values);
            }

            values.add(value);
        }

        public String getMethod() {
            return method;
        }

        @Override
        public void setMethod(String method) {
            this.method = method;
        }

        public String getUri() {
            return uri;
        }

        @Override
        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getProtocol() {
            return protocol;
        }

        @Override
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public int getParameterCount() {
            return parameters.size();
        }

        public String getParameter(String name, int i) {
            List<String> values = parameters.get(name);
            if (values == null) {
                return null;
            }
            return values.get(i);
        }
    }
}
