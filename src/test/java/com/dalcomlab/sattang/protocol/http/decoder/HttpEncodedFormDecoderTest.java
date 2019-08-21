package com.dalcomlab.sattang.protocol.http.decoder;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HttpEncodedFormDecoderTest {


    @Test
    public void testBasic() throws Exception {
        String httpForm = "A=A&B=B&C=C&D=D";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), "A");
        assertEquals(parameters.get("B").get(0), "B");
        assertEquals(parameters.get("C").get(0), "C");
        assertEquals(parameters.get("D").get(0), "D");
    }


    @Test
    public void testBasic_SimulateAsync() throws Exception {
        String httpForm = "A=A&B=B&C=C&D=D";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), "A");
        assertEquals(parameters.get("B").get(0), "B");
        assertEquals(parameters.get("C").get(0), "C");
        assertEquals(parameters.get("D").get(0), "D");
    }


    @Test
    public void testFormValue_Multiple() throws Exception {
        String httpForm = "A=A1&B=B1&C=C1&D=D1&A=A2&B=B2&C=C2&D=D2";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 2);
        assertEquals(parameters.get("B").size(), 2);
        assertEquals(parameters.get("C").size(), 2);
        assertEquals(parameters.get("D").size(), 2);

        assertEquals(parameters.get("A").get(0), "A1");
        assertEquals(parameters.get("B").get(0), "B1");
        assertEquals(parameters.get("C").get(0), "C1");
        assertEquals(parameters.get("D").get(0), "D1");

        assertEquals(parameters.get("A").get(1), "A2");
        assertEquals(parameters.get("B").get(1), "B2");
        assertEquals(parameters.get("C").get(1), "C2");
        assertEquals(parameters.get("D").get(1), "D2");
    }


    @Test
    public void testFormValue_Multiple_SimulateAsync() throws Exception {
        String httpForm = "A=A1&B=B1&C=C1&D=D1&A=A2&B=B2&C=C2&D=D2";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 2);
        assertEquals(parameters.get("B").size(), 2);
        assertEquals(parameters.get("C").size(), 2);
        assertEquals(parameters.get("D").size(), 2);

        assertEquals(parameters.get("A").get(0), "A1");
        assertEquals(parameters.get("B").get(0), "B1");
        assertEquals(parameters.get("C").get(0), "C1");
        assertEquals(parameters.get("D").get(0), "D1");

        assertEquals(parameters.get("A").get(1), "A2");
        assertEquals(parameters.get("B").get(1), "B2");
        assertEquals(parameters.get("C").get(1), "C2");
        assertEquals(parameters.get("D").get(1), "D2");
    }


    @Test
    public void testFormValue_Empty() throws Exception {
        String httpForm = "A=&B=&C=&D=";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), "");
        assertEquals(parameters.get("B").get(0), "");
        assertEquals(parameters.get("C").get(0), "");
        assertEquals(parameters.get("D").get(0), "");
    }


    @Test
    public void testFormValue_Empty_SimulateAsync() throws Exception {
        String httpForm = "A=&B=&C=&D=";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), "");
        assertEquals(parameters.get("B").get(0), "");
        assertEquals(parameters.get("C").get(0), "");
        assertEquals(parameters.get("D").get(0), "");
    }


    @Test
    public void testFormValue_IncludeWhiteSpace() throws Exception {
        String httpForm = "A= A &B=\tB\t&C=\tC &D= D\t";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), " A ");
        assertEquals(parameters.get("B").get(0), "\tB\t");
        assertEquals(parameters.get("C").get(0), "\tC ");
        assertEquals(parameters.get("D").get(0), " D\t");
    }


    @Test
    public void testFormValue_IncludeWhiteSpace_SimulateAsync() throws Exception {
        String httpForm = "A= A &B=\tB\t&C=\tC &D= D\t";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);
        assertEquals(parameters.get("C").size(), 1);
        assertEquals(parameters.get("D").size(), 1);

        assertEquals(parameters.get("A").get(0), " A ");
        assertEquals(parameters.get("B").get(0), "\tB\t");
        assertEquals(parameters.get("C").get(0), "\tC ");
        assertEquals(parameters.get("D").get(0), " D\t");
    }

    //

    @Test
    public void testFormName_IncludeWhiteSpace() throws Exception {
        String httpForm = " A =A&\tB\t=B&\tC =C& D\t=D";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey(" A "));
        assertTrue(parameters.containsKey("\tB\t"));
        assertTrue(parameters.containsKey("\tC "));
        assertTrue(parameters.containsKey(" D\t"));

        assertEquals(parameters.get(" A ").size(), 1);
        assertEquals(parameters.get("\tB\t").size(), 1);
        assertEquals(parameters.get("\tC ").size(), 1);
        assertEquals(parameters.get(" D\t").size(), 1);

        assertEquals(parameters.get(" A ").get(0), "A");
        assertEquals(parameters.get("\tB\t").get(0), "B");
        assertEquals(parameters.get("\tC ").get(0), "C");
        assertEquals(parameters.get(" D\t").get(0), "D");
    }


    @Test
    public void testFormName_IncludeWhiteSpace_SimulateAsync() throws Exception {
        String httpForm = " A =A&\tB\t=B&\tC =C& D\t=D";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey(" A "));
        assertTrue(parameters.containsKey("\tB\t"));
        assertTrue(parameters.containsKey("\tC "));
        assertTrue(parameters.containsKey(" D\t"));

        assertEquals(parameters.get(" A ").size(), 1);
        assertEquals(parameters.get("\tB\t").size(), 1);
        assertEquals(parameters.get("\tC ").size(), 1);
        assertEquals(parameters.get(" D\t").size(), 1);

        assertEquals(parameters.get(" A ").get(0), "A");
        assertEquals(parameters.get("\tB\t").get(0), "B");
        assertEquals(parameters.get("\tC ").get(0), "C");
        assertEquals(parameters.get(" D\t").get(0), "D");
    }

    @Test
    public void testForm_Empty() throws Exception {
        String httpForm = "A=A&&&&&&B=B";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();

        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(parameters.size(), 2);

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);

        assertEquals(parameters.get("A").get(0), "A");
        assertEquals(parameters.get("B").get(0), "B");
    }

    @Test
    public void testForm_Empty_SimulateAsync() throws Exception {
        String httpForm = "A=A&&&&&&B=B";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(parameters.size(), 2);

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));

        assertEquals(parameters.get("A").size(), 1);
        assertEquals(parameters.get("B").size(), 1);

        assertEquals(parameters.get("A").get(0), "A");
        assertEquals(parameters.get("B").get(0), "B");
    }


    @Test
    public void testFormValue_IncludeEqualSign() throws Exception {
        String httpForm = "A=A=&B==B&C==C=&D=D==";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(httpForm.getBytes());
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").get(0), "A=");
        assertEquals(parameters.get("B").get(0), "=B");
        assertEquals(parameters.get("C").get(0), "=C=");
        assertEquals(parameters.get("D").get(0), "D==");
    }


    @Test
    public void testFormValue_IncludeEqualSign_SimulateAsync() throws Exception {
        String httpForm = "A=A=&B==B&C==C=&D=D==";

        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            for (int i = 0; i < httpForm.length(); i++) {
                decoder.decode(new byte[]{(byte) httpForm.charAt(i)});
            }
            decoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(parameters.containsKey("A"));
        assertTrue(parameters.containsKey("B"));
        assertTrue(parameters.containsKey("C"));
        assertTrue(parameters.containsKey("D"));

        assertEquals(parameters.get("A").get(0), "A=");
        assertEquals(parameters.get("B").get(0), "=B");
        assertEquals(parameters.get("C").get(0), "=C=");
        assertEquals(parameters.get("D").get(0), "D==");
    }


    @Test
    public void testValueContainsBackSlash() {
        String data = "sum=10%5c2%3d5";
        Map<String, List<String>> parameters = new HashMap<>();
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
//        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
//        try {
//            parser.read(data.getBytes());
//            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            for (int i = 0; i < data.length(); i++) {
                decoder.decode(new byte[]{(byte) data.charAt(i)});
            }
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            decoder.decode(data.getBytes());
            decoder.close();
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
        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        try {
            decoder.listen(parameters);
            for (int i = 0; i < data.length(); i++) {
                decoder.decode(new byte[]{(byte) data.charAt(i)});
            }
            decoder.close();
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
