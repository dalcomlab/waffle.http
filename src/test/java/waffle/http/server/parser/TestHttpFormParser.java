package waffle.http.server.parser;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestHttpFormParser {

    @Test
    public void testBasic() {
        String data = "a=a&b=b&c=c&d=d";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));
        assertTrue(parameters.containsKey("c"));
        assertTrue(parameters.containsKey("d"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);
        assertEquals(parameters.get("c").size(), 1);
        assertEquals(parameters.get("d").size(), 1);

        assertEquals(parameters.get("a").get(0), "a");
        assertEquals(parameters.get("b").get(0), "b");
        assertEquals(parameters.get("c").get(0), "c");
        assertEquals(parameters.get("d").get(0), "d");
    }


    @Test
    public void testBasic_SingleByte() {
        String data = "a=a&b=b&c=c&d=d";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            for (int i = 0; i < data.length(); i++) {
                parser.parse(new byte[]{(byte) data.charAt(i)});
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));
        assertTrue(parameters.containsKey("c"));
        assertTrue(parameters.containsKey("d"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);
        assertEquals(parameters.get("c").size(), 1);
        assertEquals(parameters.get("d").size(), 1);

        assertEquals(parameters.get("a").get(0), "a");
        assertEquals(parameters.get("b").get(0), "b");
        assertEquals(parameters.get("c").get(0), "c");
        assertEquals(parameters.get("d").get(0), "d");
    }


    @Test
    public void testNameContainsMultipleValues() {
        String data = "a=a1&b=b1&c=c1&d=d1&a=a2&b=b2&c=c2&d=d2";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));
        assertTrue(parameters.containsKey("c"));
        assertTrue(parameters.containsKey("d"));

        assertEquals(parameters.get("a").size(), 2);
        assertEquals(parameters.get("b").size(), 2);
        assertEquals(parameters.get("c").size(), 2);
        assertEquals(parameters.get("d").size(), 2);

        assertEquals(parameters.get("a").get(0), "a1");
        assertEquals(parameters.get("b").get(0), "b1");
        assertEquals(parameters.get("c").get(0), "c1");
        assertEquals(parameters.get("d").get(0), "d1");

        assertEquals(parameters.get("a").get(1), "a2");
        assertEquals(parameters.get("b").get(1), "b2");
        assertEquals(parameters.get("c").get(1), "c2");
        assertEquals(parameters.get("d").get(1), "d2");
    }


    @Test
    public void testNameContainsMultipleValues_SingleByte() {
        String data = "a=a1&b=b1&c=c1&d=d1&a=a2&b=b2&c=c2&d=d2";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            for (int i = 0; i < data.length(); i++) {
                parser.parse(new byte[]{(byte) data.charAt(i)});
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));
        assertTrue(parameters.containsKey("c"));
        assertTrue(parameters.containsKey("d"));

        assertEquals(parameters.get("a").size(), 2);
        assertEquals(parameters.get("b").size(), 2);
        assertEquals(parameters.get("c").size(), 2);
        assertEquals(parameters.get("d").size(), 2);

        assertEquals(parameters.get("a").get(0), "a1");
        assertEquals(parameters.get("b").get(0), "b1");
        assertEquals(parameters.get("c").get(0), "c1");
        assertEquals(parameters.get("d").get(0), "d1");

        assertEquals(parameters.get("a").get(1), "a2");
        assertEquals(parameters.get("b").get(1), "b2");
        assertEquals(parameters.get("c").get(1), "c2");
        assertEquals(parameters.get("d").get(1), "d2");
    }


    @Test
    public void testNameContainsNoValue() {
        String data = "a=&b=&c=&d=";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));
        assertTrue(parameters.containsKey("c"));
        assertTrue(parameters.containsKey("d"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);
        assertEquals(parameters.get("c").size(), 1);
        assertEquals(parameters.get("d").size(), 1);

        assertEquals(parameters.get("a").get(0), "");
        assertEquals(parameters.get("b").get(0), "");
        assertEquals(parameters.get("c").get(0), "");
        assertEquals(parameters.get("d").get(0), "");
    }

    @Test
    public void testEmptyPair() {
        String data = "a=a&&&&&&b=b";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(parameters.size(), 2);

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);

        assertEquals(parameters.get("a").get(0), "a");
        assertEquals(parameters.get("b").get(0), "b");
    }


    @Test
    public void testValueContainsEqualSign() {
        String data = "foo=bar=&foo=baz=&foo=baz=bar";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("foo"));

        assertEquals(parameters.get("foo").size(), 3);

        assertEquals(parameters.get("foo").get(0), "bar=");
        assertEquals(parameters.get("foo").get(1), "baz=");
        assertEquals(parameters.get("foo").get(2), "baz=bar");
    }


    @Test
    public void testValueContainsBackSlash() {
        String data = "sum=10%5c2%3d5";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("sum"));

        assertEquals(parameters.get("sum").size(), 1);

        assertEquals(parameters.get("sum").get(0), "10\\2=5");

    }


    @Test
    public void testValueContainsDoubleQuoteAndSpace() {
        String data = "fruits=\"apple%20banana\"";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("fruits"));

        assertEquals(parameters.get("fruits").size(), 1);

        assertEquals(parameters.get("fruits").get(0), "\"apple banana\"");

    }

//
//    @Test
//    public void testValueContainsBadlyFormedCharacter() {
//        String data = "first=%41&second=%a&third=%b";
//        Map<String, List<String>> parameters = new HashMap<>();
//        HttpFormParser parser = new HttpFormParser(parameters);
//        try {
//            parser.parse(data.getBytes());
//            parser.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        assertTrue(parameters.containsKey("first"));
//        assertTrue(parameters.containsKey("second"));
//        assertTrue(parameters.containsKey("third"));
//
//        assertEquals(parameters.get("first").size(), 1);
//        assertEquals(parameters.get("second").size(), 1);
//        assertEquals(parameters.get("third").size(), 1);
//
//        assertEquals(parameters.get("first").get(0), "A");
//        assertEquals(parameters.get("second").get(1), "%a");
//        assertEquals(parameters.get("third").get(2), "%b");
//    }


    @Test
    public void testValueContainsSpace() {
        String data = "type=office%20365&type=office+365";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("type"));

        assertEquals(parameters.get("type").size(), 2);

        assertEquals(parameters.get("type").get(0), "office 365");
        assertEquals(parameters.get("type").get(1), "office 365");
    }


    @Test
    public void testValueContainsSingleQuote() {
        String data = "name=Bill&name=O%27Reilly";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("name"));

        assertEquals(parameters.get("name").size(), 2);

        assertEquals(parameters.get("name").get(0), "Bill");
        assertEquals(parameters.get("name").get(1), "O'Reilly");
    }


    @Test
    public void testValueIsUrlEncoded() {
        String data = "a=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94&b=%EB%B0%98%EA%B0%91%EC%8A%B5%EB%8B%88%EB%8B%A4";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);

        assertEquals(parameters.get("a").get(0), "안녕하세요");
        assertEquals(parameters.get("b").get(0), "반갑습니다");
    }


    @Test
    public void testValueIsUrlEncoded_SingleByte() {
        String data = "a=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94&b=%EB%B0%98%EA%B0%91%EC%8A%B5%EB%8B%88%EB%8B%A4";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            for (int i = 0; i < data.length(); i++) {
                parser.parse(new byte[]{(byte) data.charAt(i)});
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("a"));
        assertTrue(parameters.containsKey("b"));

        assertEquals(parameters.get("a").size(), 1);
        assertEquals(parameters.get("b").size(), 1);

        assertEquals(parameters.get("a").get(0), "안녕하세요");
        assertEquals(parameters.get("b").get(0), "반갑습니다");
    }


    @Test
    public void testNameAndValueIsUrlEncoded() {
        String data = "%EC%B2%AB%EB%B2%88%EC%A7%B8=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94&%EB%91%90%EB%B2%88%EC%A7%B8=%EB%B0%98%EA%B0%91%EC%8A%B5%EB%8B%88%EB%8B%A4";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            parser.parse(data.getBytes());
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("첫번째"));
        assertTrue(parameters.containsKey("두번째"));

        assertEquals(parameters.get("첫번째").size(), 1);
        assertEquals(parameters.get("두번째").size(), 1);

        assertEquals(parameters.get("첫번째").get(0), "안녕하세요");
        assertEquals(parameters.get("두번째").get(0), "반갑습니다");
    }


    @Test
    public void testNameAndValueIsUrlEncoded_SingleByte() {
        String data = "%EC%B2%AB%EB%B2%88%EC%A7%B8=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94&%EB%91%90%EB%B2%88%EC%A7%B8=%EB%B0%98%EA%B0%91%EC%8A%B5%EB%8B%88%EB%8B%A4";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpFormParser parser = new HttpFormParser(parameters);
        try {
            for (int i = 0; i < data.length(); i++) {
                parser.parse(new byte[]{(byte) data.charAt(i)});
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("첫번째"));
        assertTrue(parameters.containsKey("두번째"));

        assertEquals(parameters.get("첫번째").size(), 1);
        assertEquals(parameters.get("두번째").size(), 1);

        assertEquals(parameters.get("첫번째").get(0), "안녕하세요");
        assertEquals(parameters.get("두번째").get(0), "반갑습니다");
    }

}
