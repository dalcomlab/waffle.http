package com.dalcomlab.sattang.protocol.http.decoder;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class HttpAttributeDecoderTest {


    private HttpAttributeDecoder createDecoder() {
        return new HttpAttributeDecoder();
    }


    @Test
    public void testParseSingleValueNothing_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode(";", ';');

        assertTrue(parameter.isEmpty());
    }


    @Test
    public void testParseSingleValueNothing_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("  ;  ", ';');

        assertTrue(parameter.isEmpty());
    }

    @Test
    public void testParseSingleValueNothing_03() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode(";  ;  ;", ';');

        assertTrue(parameter.isEmpty());
    }


    @Test
    public void testParseSingleValueNothing_04() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("=", ';');
        assertTrue(parameter.isEmpty());
    }


    @Test
    public void testParseSingleValueNothing_05() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("  =  ", ';');
        assertTrue(parameter.isEmpty());
    }


    @Test
    public void testParseSingleValueNothing_06() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("  =   =  ", ';');
        assertTrue(parameter.isEmpty());
    }

    @Test
    public void testParseSingleValueNothing_07() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode(";=;  = ;;  =  ;;", ';');
        assertTrue(parameter.isEmpty());
    }


    @Test
    public void testParseSingleValueSingleText() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a", ';');

        assertEquals(parameter.get("a"), null);
    }

    @Test
    public void testParseSingleValueGeneral_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a", ';');

        assertTrue(parameter.containsKey("a"));

        assertEquals("a", parameter.get("a"));
    }

    @Test
    public void testParseSingleValueGeneral_011() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("aa=aa", ';');

        assertTrue(parameter.containsKey("aa"));

        assertEquals("aa", parameter.get("aa"));
    }


    @Test
    public void testParseSingleValueGeneral_012() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("aaa=aaa", ';');

        assertTrue(parameter.containsKey("aaa"));

        assertEquals("aaa", parameter.get("aaa"));
    }

    @Test
    public void testParseSingleValueGeneral_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=", ';');

        assertTrue(parameter.containsKey("a"));
        assertEquals(parameter.get("a"), null);
    }

    @Test
    public void testParseSingleValueGeneral_03() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a;b=b;c=c", ';');

        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));
        assertTrue(parameter.containsKey("c"));

        assertEquals("a", parameter.get("a"));
        assertEquals("b", parameter.get("b"));
        assertEquals("c", parameter.get("c"));
    }

    @Test
    public void testParseSingleValueIncludeEqualCharacter() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=b=c=d=e=f", ';');
        assertTrue(parameter.containsKey("a"));
        assertEquals(parameter.get("a"), "b=c=d=e=f");

        parameter = decoder.decode("a[>=]=23", ';');
        assertTrue(parameter.containsKey("a[>"));
        assertEquals(parameter.get("a[>"), "]=23");

        parameter = decoder.decode("a[<=>]==23", ';');
        assertTrue(parameter.containsKey("a[<"));
        assertEquals(parameter.get("a[<"), ">]==23");

        parameter = decoder.decode("a[==]=23", ';');
        assertTrue(parameter.containsKey("a["));
        assertEquals(parameter.get("a["), "=]=23");
    }

    @Test
    public void testParseSingleValueJsonFormat() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a.b=c", '&');
        assertTrue(parameter.containsKey("a.b"));
        assertEquals(parameter.get("a.b"), "c");

        parameter = decoder.decode("a[b]=c", '&');
        assertTrue(parameter.containsKey("a[b]"));
        assertEquals(parameter.get("a[b]"), "c");

        parameter = decoder.decode("a[b][c]=d", '&');
        assertTrue(parameter.containsKey("a[b][c]"));
        assertEquals(parameter.get("a[b][c]"), "d");

        parameter = decoder.decode("a[]=b&a[]=c", '&');
        assertTrue(parameter.containsKey("a[]"));
        assertEquals(parameter.get("a[]"), "c");

        parameter = decoder.decode("operators=[\">=\", \"<=\"]", '&');
        assertTrue(parameter.containsKey("operators"));
        assertEquals(parameter.get("operators"), "[\">=\", \"<=\"]");
    }


    @Test
    public void testParseSingleValueSameName_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a;a=c;a=d", ';');
        assertTrue(parameter.containsKey("a"));
        assertEquals(parameter.get("a"), "d");
    }

    @Test
    public void testParseSingleValueSameName_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a;a=c;a=d;a=", ';');
        assertTrue(parameter.containsKey("a"));
        assertEquals(parameter.get("a"), null);
    }


    @Test
    public void testParseSingleValueTrim_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a  =  a   ;   b   =   b   ;  c  =  c", ';');

        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));
        assertTrue(parameter.containsKey("c"));

        assertEquals(parameter.get("a"), "a");
        assertEquals(parameter.get("b"), "b");
        assertEquals(parameter.get("c"), "c");
    }


    @Test
    public void testParseSingleValueTrim_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a    a;b=   b    b   ;  c  =  c    c", ';');
        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));
        assertTrue(parameter.containsKey("c"));

        assertEquals(parameter.get("a"), "a    a");
        assertEquals(parameter.get("b"), "b    b");
        assertEquals(parameter.get("c"), "c    c");
    }


    @Test
    public void testParseSingleValueTrim_03() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=\u0085\u000C\u2028\u2029 Hello World \u0085\u000C\u2028\u2029;b=b", ';');
        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));

        assertEquals(parameter.get("a"), "Hello World");
        assertEquals(parameter.get("b"), "b");
    }


    @Test
    public void testParseSingleValueQuotedString_00() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("\"a\"=\"a\"", ';');
        assertTrue(parameter.containsKey("a"));

        assertEquals(parameter.get("a"), "a");

    }

    @Test
    public void testParseSingleValueQuotedString_0001() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=\"a", ';');
        assertTrue(parameter.containsKey("a"));

        assertEquals(parameter.get("a"), "\"a");

    }

    @Test
    public void testParseSingleValueQuotedString_000() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("\"a\"=\"\"", ';');
        assertTrue(parameter.containsKey("a"));

        assertEquals(parameter.get("a"), null);

    }


    @Test
    public void testParseSingleValueQuotedString_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("\"a\"=\"a\";\"b\"=\"b\";\"c\"=\"c\"", ';');
        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));
        assertTrue(parameter.containsKey("c"));


        assertEquals(parameter.get("a"), "a");
        assertEquals(parameter.get("b"), "b");
        assertEquals(parameter.get("c"), "c");

    }

    @Test
    public void testParseSingleValueQuotedString_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=stuff;b=\"stuff;stuff\";c=\"a=stuff;b=stuff\";d=\"\"stuff\"\";e=s\"t\"u\"f\"f;f=\"stuff", ';');
        assertTrue(parameter.containsKey("a"));
        assertTrue(parameter.containsKey("b"));
        assertTrue(parameter.containsKey("c"));
        assertTrue(parameter.containsKey("d"));
        assertTrue(parameter.containsKey("e"));

        assertEquals(parameter.get("a"), "stuff");
        assertEquals(parameter.get("b"), "stuff;stuff");
        assertEquals(parameter.get("c"), "a=stuff;b=stuff");
        assertEquals(parameter.get("d"), "\"stuff\"");
        assertEquals(parameter.get("e"), "s\"t\"u\"f\"f");
        assertEquals(parameter.get("f"), "\"stuff");
    }

    @Test
    public void testParseSingleValueQuotedString_04() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=\"a;b=b;c=c", ';');
        assertEquals(parameter.get("a"), "\"a;b=b;c=c");
    }


    @Test
    public void testParseSingleValueQuotedString_05() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a\"a;b=b;c=c", ';');
        assertEquals(parameter.get("a"), "a\"a;b=b;c=c");
    }


    @Test
    public void testParseSingleValueQuotedString_06() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> map = decoder.decode("a=a\"a\"b\";b=b;c=c", ';');
        assertEquals(map.get("a"), "a\"a\"b\";b=b;c=c");
    }

    @Test
    public void testParseSingleValueQuotedString_07() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("\"a\\=x=x;b\"=y=y;\"c\"=z=z", ';');

        assertEquals(parameter.get("\"a\\"), "x=x");
        assertEquals(parameter.get("b\""), "y=y");
        assertEquals(parameter.get("c"), "z=z");

    }


    @Test
    public void testParseSingleValueEscapeString_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a\\=a=b;b=b\\;c=c", ';');

        assertEquals(parameter.get("a\\"), "a=b");
        assertEquals(parameter.get("b"), "b\\");
        assertEquals(parameter.get("c"), "c");
    }


    @Test
    public void testParseSingleValueEscapeString_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("a=a\\\"a=b;b=b", ';');
        assertEquals(parameter.get("a"), "a\\\"a=b");
        assertEquals(parameter.get("b"), "b");

    }


    @Test
    public void testParseSingleValueFindBoundaryFromHeader() {
        HttpAttributeDecoder decoder = createDecoder();
        String s = "Content-type: multipart/form-data, boundary=AaB03x";
        Map<String, String> parameter = decoder.decode(s, ',');
        assertEquals(parameter.get("boundary"), "AaB03x");

        s = "Content-type: multipart/mixed, boundary=BbC04y";
        parameter = decoder.decode(s, ',');
        assertEquals(parameter.get("boundary"), "BbC04y");
    }


    @Test
    public void testParseSingleValueHttpCacheControl() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("no-store, no-cache, must-revalidate, post-check=0, pre-check=0", ',');
        assertTrue(parameter.containsKey("no-store"));
        assertTrue(parameter.containsKey("no-cache"));
        assertTrue(parameter.containsKey("must-revalidate"));
        assertTrue(parameter.containsKey("post-check"));
        assertTrue(parameter.containsKey("pre-check"));

        assertEquals(parameter.get("no-store"), null);
        assertEquals(parameter.get("no-cache"), null);
        assertEquals(parameter.get("must-revalidate"), null);
        assertEquals(parameter.get("post-check"), "0");
        assertEquals(parameter.get("pre-check"), "0");
    }

    @Test
    public void testParseSingleValueContentDisposition_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("Content-Disposition: form-data; name=\"fieldName\"; filename=\"filename.jpg\"", ';');
        assertTrue(parameter.containsKey("name"));
        assertTrue(parameter.containsKey("filename"));

        assertEquals(parameter.get("name"), "fieldName");
        assertEquals(parameter.get("filename"), "filename.jpg");
    }

    @Test
    public void testParseSingleValueContentDisposition_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> parameter = decoder.decode("Content-Disposition: form-data; name=image; filename=\"12348024_1150631324960893_344096225642532672_n.jpg\"", ';');
        assertTrue(parameter.containsKey("name"));
        assertTrue(parameter.containsKey("filename"));

        assertEquals(parameter.get("name"), "image");
        assertEquals(parameter.get("filename"), "12348024_1150631324960893_344096225642532672_n.jpg");
    }



    @Test
    public void testParseMultiValueGeneral() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, List<String>> map = decoder.decodeMultipleValue("a=a,a=b,a=c", ',');

        assertTrue(map.containsKey("a"));
        assertTrue(map.get("a").size() == 3);

        assertEquals(map.get("a").get(0), "a");
        assertEquals(map.get("a").get(1), "b");
        assertEquals(map.get("a").get(2), "c");
    }


    @Test
    public void testParseMultiValueQueryString_01() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, List<String>> map = decoder.decodeMultipleValue("a=a&b=b&c=c&a=aa&b=bb&c=cc&a=aaa&b=bbb", '&');

        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));

        assertEquals(map.get("a").size(), 3);
        assertEquals(map.get("b").size(), 3);
        assertEquals(map.get("c").size(), 2);

        assertEquals(map.get("a").get(0), "a");
        assertEquals(map.get("a").get(1), "aa");
        assertEquals(map.get("a").get(2), "aaa");

        assertEquals(map.get("b").get(0), "b");
        assertEquals(map.get("b").get(1), "bb");
        assertEquals(map.get("b").get(2), "bbb");

        assertEquals(map.get("c").get(0), "c");
        assertEquals(map.get("c").get(1), "cc");
    }

    @Test
    public void testParseMultiValueQueryString_02() {
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, List<String>> params = decoder.decodeMultipleValue("list_a=1&list_a=2&list_a=3&list_b[]=1&list_b[]=2&list_b[]=3&list_c=1,2,3", '&');

        assertTrue(params.containsKey("list_a"));
        assertTrue(params.containsKey("list_b[]"));
        assertTrue(params.containsKey("list_c"));

        assertEquals(params.get("list_a").size(), 3);
        assertEquals(params.get("list_b[]").size(), 3);
        assertEquals(params.get("list_c").size(), 1);

        assertEquals(params.get("list_a").get(0), "1");
        assertEquals(params.get("list_a").get(1), "2");
        assertEquals(params.get("list_a").get(2), "3");
        assertEquals(params.get("list_b[]").get(0), "1");
        assertEquals(params.get("list_b[]").get(1), "2");
        assertEquals(params.get("list_b[]").get(2), "3");
        assertEquals(params.get("list_c").get(0), "1,2,3");
    }

    @Test
    public void testCookie() {
        String source =
                "ARPT=LWUKQPSWRTUN04CKKJI; " +
                        "kw-2E343B92-B097-442c-BFA5-BE371E0325A2=unfinished_furniture; " +
                        "__utma=48461872.1094088325.1258140131.1258140131.1258140131.1; " +
                        "__utmb=48461872.13.10.1258140131; __utmc=48461872; " +
                        "__utmz=48461872.1258140131.1.1.utmcsr=overstock.com|utmccn=(referral)|" +
                        "utmcmd=referral|utmcct=/Home-Garden/Furniture/Clearance/clearance/32/dept.html";
       // System.out.print(source);
        HttpAttributeDecoder decoder = createDecoder();
        Map<String, String> params = decoder.decode(source, ';');
        params.forEach((name, value) -> {
            System.out.println(name + " : " + value);
        });
    }
}
