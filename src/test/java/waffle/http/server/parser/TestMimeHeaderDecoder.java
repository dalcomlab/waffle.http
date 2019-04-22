package waffle.http.server.parser;

import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

import static org.testng.Assert.assertEquals;

public class TestMimeHeaderDecoder {

    private MimeHeaderDecoder mimeHeaderDecoder = new MimeHeaderDecoder();

    @Test
    public void testOne() {
        String data = "=?utf-8?B?7Jyg67OR7LC9?= <ryubc@clipsoft.co.kr>";
        String result = mimeHeaderDecoder.decode(data);

        //assertEquals(result, "유병창 <ryubc@clipsoft.co.kr>");
    }

    @Test
    public void testTwo() {
        String data = "=?ISO-8859-1?B?SWYgeW91IGNhbiByZWFkIHRoaXMgeW8=?= =?ISO-8859-2?B?dSB1bmRlcnN0YW5kIHRoZSBleGFtcGxlLg==?=";
        String result = mimeHeaderDecoder.decode(data);

        assertEquals(result, "If you can read this you understand the example.");
    }


    @Test
    public void testLinearWhiteSpace() {
        String data = "Subject: =?ISO-8859-1?B?SWYgeW91IGNhbiByZWFkIHRoaXMgeW8=?=        =?ISO-8859-2?B?dSB1bmRlcnN0YW5kIHRoZSBleGFtcGxlLg==?=";
        String result = mimeHeaderDecoder.decode(data);

        assertEquals(result, "Subject: If you can read this you understand the example.");
    }

    @Test
    public void testSayHelloQuotedPrintable() throws UnsupportedEncodingException {
        String data = "Message: =?UTF-8?Q?=EC=95=88=EB=85=95=ED=95=98=EC=84=B8=EC=9A=94?=";
        String result = mimeHeaderDecoder.decode(data);

       // assertEquals(result, "Message: 안녕하세요");
    }


    @Test
    public void testSayHelloBase64() {
        String data = "Message: =?UTF-8?B?7JWI64WV7ZWY7IS47JqU?=";
        String result = mimeHeaderDecoder.decode(data);
        //assertEquals(result, "Message: 안녕하세요");
    }

}
