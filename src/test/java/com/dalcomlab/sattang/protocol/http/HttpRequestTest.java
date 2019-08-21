package com.dalcomlab.sattang.protocol.http;

import static org.junit.Assert.assertEquals;

public class HttpRequestTest {
//
//    @BeforeClass
//    public static void setup() {
//    }
//
//
//    @Test
//    public void testGetContentType_01() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addHeader("Content-Type", "text/html");
//
//        assertEquals(request.getContentType(), "text/html");
//    }
//
//
//    @Test
//    public void testGetContentType_02() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addHeader("Content-Type", "image/png");
//
//        assertEquals(request.getContentType(), "image/png");
//    }
//
//    @Test
//    public void testGetContentLength_01() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        assertEquals(request.getContentLength(), -1);
//    }
//
//
//    @Test
//    public void testGetContentLength_02() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addHeader("Content-Length", "12345");
//
//        assertEquals(request.getContentLength(), 12345);
//    }
//
//
//    @Test
//    public void testAddParameter_01() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//        request.addParameter("C", "C");
//        request.addParameter("D", "D");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), "C");
//        assertEquals(request.getParameter("D"), "D");
//    }
//
//
//    @Test
//    public void testAddParameter_02() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//        request.addParameter("C", "C");
//        request.addParameter("D", "D");
//
//        Map<String, String[]> map = request.getParameterMap();
//
//        assertEquals(map.size(), 4);
//
//        assertEquals(map.get("A").length, 1);
//        assertEquals(map.get("B").length, 1);
//        assertEquals(map.get("C").length, 1);
//        assertEquals(map.get("D").length, 1);
//
//        assertEquals(map.get("A")[0], "A");
//        assertEquals(map.get("B")[0], "B");
//        assertEquals(map.get("C")[0], "C");
//        assertEquals(map.get("D")[0], "D");
//    }
//
//
//
//    @Test
//    public void testAddParameter_03() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addParameter("A", "A");
//        request.addParameter("A", "B");
//        request.addParameter("A", "C");
//        request.addParameter("A", "D");
//
//        Map<String, String[]> map = request.getParameterMap();
//
//        assertEquals(map.size(), 1);
//
//        assertEquals(map.get("A").length, 4);
//
//        assertEquals(map.get("A")[0], "A");
//        assertEquals(map.get("A")[1], "B");
//        assertEquals(map.get("A")[2], "C");
//        assertEquals(map.get("A")[3], "D");
//    }
//
//
//    @Test
//    public void testAddParameter_04() throws Exception {
//        HttpRequest request = new DefaultHttpRequest(null);
//        request.addParameter("A", "A");
//        request.addParameter("A", "B");
//        request.addParameter("B", "C");
//        request.addParameter("B", "D");
//
//        Map<String, String[]> map = request.getParameterMap();
//
//        assertEquals(map.size(), 2);
//
//        assertEquals(map.get("A").length, 2);
//        assertEquals(map.get("B").length, 2);
//
//        assertEquals(map.get("A")[0], "A");
//        assertEquals(map.get("A")[1], "B");
//        assertEquals(map.get("B")[0], "C");
//        assertEquals(map.get("B")[1], "D");
//    }
//
//
//
//    @Test
//    public void testEncodedFormParameter_And_MissingMethod() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), null);
//        assertEquals(request.getParameter("D"), null);
//    }
//
//    @Test
//    public void testEncodedFormParameter_And_GET_Method() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("GET");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), null);
//        assertEquals(request.getParameter("D"), null);
//    }
//
//    @Test
//    public void testEncodedFormParameter_And_MissingContentType() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), null);
//        assertEquals(request.getParameter("D"), null);
//    }
//
//
//    @Test
//    public void testEncodedFormParameter_And_MisMatchContentType() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addHeader("Content-Type", "text/html");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), null);
//        assertEquals(request.getParameter("D"), null);
//    }
//
//
//    @Test
//    public void testEncodedFormParameter_01() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//
//        assertEquals(request.getParameter("C"), "C");
//        assertEquals(request.getParameter("D"), "D");
//    }
//
//
//    @Test
//    public void testEncodedFormParameter_02() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        assertEquals(request.getParameter("A"), "A");
//        assertEquals(request.getParameter("B"), "B");
//        assertEquals(request.getParameter("C"), "C");
//        assertEquals(request.getParameter("D"), "D");
//    }
//
//
//    @Test
//    public void testAddParameter_And_EncodedFormParameter_02() throws Exception {
//        String httpForm ="C=C&D=D";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        request.addParameter("A", "A");
//        request.addParameter("B", "B");
//
//        Map<String, String[]> map = request.getParameterMap();
//
//        assertEquals(map.size(), 4);
//
//        assertEquals(map.get("A").length, 1);
//        assertEquals(map.get("B").length, 1);
//        assertEquals(map.get("C").length, 1);
//        assertEquals(map.get("D").length, 1);
//
//        assertEquals(map.get("A")[0], "A");
//        assertEquals(map.get("B")[0], "B");
//        assertEquals(map.get("C")[0], "C");
//        assertEquals(map.get("D")[0], "D");
//    }
//
//
//    @Test
//    public void testEncodedFormParameter_InputStream() throws Exception {
//        String httpForm ="A=A&B=B";
//        HttpRequest request = new DefaultHttpRequest(new ByteArrayInputStream(httpForm.getBytes()));
//        request.setMethod("POST");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//
//        request.getInputStream();
//
//        assertEquals(request.getParameter("A"), null);
//        assertEquals(request.getParameter("B"), null);
//    }
}
