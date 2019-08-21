package com.dalcomlab.sattang.protocol.http.decoder;

import com.dalcomlab.sattang.protocol.HttpTooLongException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HttpHeaderDecoderTest {

    @Test
    public void testBasic() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }


    @Test
    public void testBasic_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }

    @Test
    public void testNoCr() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\n";
        httpHeader += "B:B\n";
        httpHeader += "C:C\n";
        httpHeader += "D:D\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }


    @Test
    public void testNoCr_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\n";
        httpHeader += "B:B\n";
        httpHeader += "C:C\n";
        httpHeader += "D:D\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }


    @Test
    public void testHeaderName_Empty_Is_Allowed() throws Exception {
        String httpHeader = "";
        httpHeader += ":A\r\n";  // <- name is empty
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey(""));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get(""), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }


    @Test
    public void testHeaderName_Empty_Is_Allowed_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += ":A\r\n";  // <- name is empty
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey(""));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get(""), "A");
        assertEquals(headers.get("B"), "B");
        assertEquals(headers.get("C"), "C");
        assertEquals(headers.get("D"), "D");
    }

    @Test
    public void testHeaderName_Has_MultipleValues() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "A:B\r\n";
        httpHeader += "A:C\r\n";
        httpHeader += "A:D\r\n";

        Map<String, List<String>> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen((name, value) -> {
            if (!headers.containsKey(name)) {
                headers.put(name, new ArrayList());
            }
            headers.get(name).add(value);
        });
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertEquals(headers.size(), 1);
        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A").get(0), "A");
        assertEquals(headers.get("A").get(1), "B");
        assertEquals(headers.get("A").get(2), "C");
        assertEquals(headers.get("A").get(3), "D");
    }


    @Test
    public void testHeaderName_Has_MultipleValues_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "A:B\r\n";
        httpHeader += "A:C\r\n";
        httpHeader += "A:D\r\n";

        Map<String, List<String>> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen((name, value) -> {
            if (!headers.containsKey(name)) {
                headers.put(name, new ArrayList());
            }
            headers.get(name).add(value);
        });
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertEquals(headers.size(), 1);
        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A").get(0), "A");
        assertEquals(headers.get("A").get(1), "B");
        assertEquals(headers.get("A").get(2), "C");
        assertEquals(headers.get("A").get(3), "D");
    }


    @Test
    public void testHeaderValue_Empty_Is_Allowed() throws Exception {
        String httpHeader = "";
        httpHeader += "A:\r\n";
        httpHeader += "B:\r\n";
        httpHeader += "C:\r\n";
        httpHeader += "D:\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "");
        assertEquals(headers.get("B"), "");
        assertEquals(headers.get("C"), "");
        assertEquals(headers.get("D"), "");
    }


    @Test
    public void testHeaderValue_Empty_Is_Allowed_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:\r\n";
        httpHeader += "B:\r\n";
        httpHeader += "C:\r\n";
        httpHeader += "D:\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "");
        assertEquals(headers.get("B"), "");
        assertEquals(headers.get("C"), "");
        assertEquals(headers.get("D"), "");
    }

    //

    @Test
    public void testHeaderValue_IncludeColon() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A:A:A:\r\n";
        httpHeader += "B::B:B:B\r\n";
        httpHeader += "C::C:C:C:\r\n";
        httpHeader += "D::::\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A:A:A:");
        assertEquals(headers.get("B"), ":B:B:B");
        assertEquals(headers.get("C"), ":C:C:C:");
        assertEquals(headers.get("D"), ":::");
    }


    @Test
    public void testHeaderValue_IncludeColon_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A:A:A:\r\n";
        httpHeader += "B::B:B:B\r\n";
        httpHeader += "C::C:C:C:\r\n";
        httpHeader += "D::::\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A:A:A:");
        assertEquals(headers.get("B"), ":B:B:B");
        assertEquals(headers.get("C"), ":C:C:C:");
        assertEquals(headers.get("D"), ":::");
    }


    @Test
    public void testHeaderValue_Trim_01() throws Exception {
        String httpHeader = "";
        httpHeader += "A: A    A \r\n";
        httpHeader += "B:\tB    B\t\r\n";
        httpHeader += "C: C    C\t\r\n";
        httpHeader += "D:\tD    D \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A    A");
        assertEquals(headers.get("B"), "B    B");
        assertEquals(headers.get("C"), "C    C");
        assertEquals(headers.get("D"), "D    D");
    }

    @Test
    public void testHeaderValue_Trim_01_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A: A    A \r\n";
        httpHeader += "B:\tB    B\t\r\n";
        httpHeader += "C: C    C\t\r\n";
        httpHeader += "D:\tD    D \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A    A");
        assertEquals(headers.get("B"), "B    B");
        assertEquals(headers.get("C"), "C    C");
        assertEquals(headers.get("D"), "D    D");
    }

    @Test
    public void testHeaderValue_Trim_02() throws Exception {
        String httpHeader = "";
        httpHeader += "A: A\t\t\t\tA \r\n";
        httpHeader += "B:\tB\t\t\t\tB\t\r\n";
        httpHeader += "C: C\t\t\t\tC\t\r\n";
        httpHeader += "D:\tD\t\t\t\tD \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A\t\t\t\tA");
        assertEquals(headers.get("B"), "B\t\t\t\tB");
        assertEquals(headers.get("C"), "C\t\t\t\tC");
        assertEquals(headers.get("D"), "D\t\t\t\tD");
    }

    @Test
    public void testHeaderValue_Trim_02_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A: A\t\t\t\tA \r\n";
        httpHeader += "B:\tB\t\t\t\tB\t\r\n";
        httpHeader += "C: C\t\t\t\tC\t\r\n";
        httpHeader += "D:\tD\t\t\t\tD \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A\t\t\t\tA");
        assertEquals(headers.get("B"), "B\t\t\t\tB");
        assertEquals(headers.get("C"), "C\t\t\t\tC");
        assertEquals(headers.get("D"), "D\t\t\t\tD");
    }


    @Test
    public void testHeaderValue_Trim_03() throws Exception {
        String httpHeader = "";
        httpHeader += "A:    A\t\t\t\tA    \r\n";
        httpHeader += "B:\t\t\t\tB\t\t\t\tB\t\t\t\t\r\n";
        httpHeader += "C:    C\t\t\t\tC\t\t\t\t\r\n";
        httpHeader += "D:\t\t\t\tD\t\t\t\tD    \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A\t\t\t\tA");
        assertEquals(headers.get("B"), "B\t\t\t\tB");
        assertEquals(headers.get("C"), "C\t\t\t\tC");
        assertEquals(headers.get("D"), "D\t\t\t\tD");
    }

    @Test
    public void testHeaderValue_Trim_03_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:    A\t\t\t\tA    \r\n";
        httpHeader += "B:\t\t\t\tB\t\t\t\tB\t\t\t\t\r\n";
        httpHeader += "C:    C\t\t\t\tC\t\t\t\t\r\n";
        httpHeader += "D:\t\t\t\tD\t\t\t\tD    \r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A\t\t\t\tA");
        assertEquals(headers.get("B"), "B\t\t\t\tB");
        assertEquals(headers.get("C"), "C\t\t\t\tC");
        assertEquals(headers.get("D"), "D\t\t\t\tD");
    }

    //

    @Test
    public void testHeaderValue_Skip_Cr() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\rA\rA\r\n";
        httpHeader += "B:\rB\r\rBB\r\n";
        httpHeader += "C:\r\rC\r\rC\r\rC\r\n";
        httpHeader += "D:DDD\r\r\r\r\r\r\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "AAA");
        assertEquals(headers.get("B"), "BBB");
        assertEquals(headers.get("C"), "CCC");
        assertEquals(headers.get("D"), "DDD");
    }

    @Test
    public void testHeaderValue_Skip_Cr_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\rA\rA\r\n";
        httpHeader += "B:\rB\r\rBB\r\n";
        httpHeader += "C:\r\rC\r\rC\r\rC\r\n";
        httpHeader += "D:DDD\r\r\r\r\r\r\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "AAA");
        assertEquals(headers.get("B"), "BBB");
        assertEquals(headers.get("C"), "CCC");
        assertEquals(headers.get("D"), "DDD");
    }


    @Test
    public void testHeaderName_Duplicate() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "A:B\r\n";
        httpHeader += "A:C\r\n";
        httpHeader += "A:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "D");
    }


    @Test
    public void testHeaderName_Duplicate_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "A:B\r\n";
        httpHeader += "A:C\r\n";
        httpHeader += "A:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "D");
    }


    @Test
    public void testFoldedHeader_JustOneSpace() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += " B\r\n";
        httpHeader += " C\r\n";
        httpHeader += " D\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A B C D");
    }

    @Test
    public void testFoldedHeader_JustOneSpace_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += " B\r\n";
        httpHeader += " C\r\n";
        httpHeader += " D\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A B C D");
    }


    @Test
    public void testFoldedHeader_MultipleSpace() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "      B\r\n";
        httpHeader += "      C\r\n";
        httpHeader += "      D\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A B C D");
    }


    @Test
    public void testFoldedHeader_MultipleSpace_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "      B\r\n";
        httpHeader += "      C\r\n";
        httpHeader += "      D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A B C D");
    }

    // TODO : Which is the right result.
    @Test
    public void testFoldedHeader_JustTabSpace() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "\tB\r\n";
        httpHeader += "\tC\r\n";
        httpHeader += "\tD\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A\tB\tC\tD");
    }

    // TODO : Which is the right result.
    @Test
    public void testFoldedHeader_JustOneTab_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "\tB\r\n";
        httpHeader += "\tC\r\n";
        httpHeader += "\tD\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertEquals(headers.get("A"), "A\tB\tC\tD");
    }

    @Test
    public void testFoldedHeader_Mixed() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += " A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "  B\r\n";
        httpHeader += "C:C C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += " D\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A A");
        assertEquals(headers.get("B"), "B B");
        assertEquals(headers.get("C"), "C C");
        assertEquals(headers.get("D"), "D D");
    }

    @Test
    public void testFoldedHeader_Mixed_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += " A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "  B\r\n";
        httpHeader += "C:C C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += " D\r\n";

        Map<String, String> headers = new HashMap<>();

        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("A"));
        assertTrue(headers.containsKey("B"));
        assertTrue(headers.containsKey("C"));
        assertTrue(headers.containsKey("D"));

        assertEquals(headers.get("A"), "A A");
        assertEquals(headers.get("B"), "B B");
        assertEquals(headers.get("C"), "C C");
        assertEquals(headers.get("D"), "D D");
    }


    @Test
    public void testHeaderName_LegalCharacter() throws Exception {
        String httpHeader = "012345678:A\r\n";
        httpHeader += "abcdefghijklmnopqrstuvwxyz:B\r\n";
        httpHeader += "ABCDEFGHIJKLMNOPQRSTUVWXYZ:C\r\n";
        httpHeader += "!#$%&'*+-.^_`|~:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        decoder.decode(httpHeader.getBytes());
        decoder.close();

        assertTrue(headers.containsKey("012345678"));
        assertTrue(headers.containsKey("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(headers.containsKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue(headers.containsKey("!#$%&'*+-.^_`|~"));

        assertEquals(headers.get("012345678"), "A");
        assertEquals(headers.get("abcdefghijklmnopqrstuvwxyz"), "B");
        assertEquals(headers.get("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), "C");
        assertEquals(headers.get("!#$%&'*+-.^_`|~"), "D");
    }


    @Test
    public void testHeaderName_LegalCharacter_SimulateAsync() throws Exception {
        String httpHeader = "012345678:A\r\n";
        httpHeader += "abcdefghijklmnopqrstuvwxyz:B\r\n";
        httpHeader += "ABCDEFGHIJKLMNOPQRSTUVWXYZ:C\r\n";
        httpHeader += "!#$%&'*+-.^_`|~:D\r\n";

        Map<String, String> headers = new HashMap<>();
        HttpHeaderDecoder decoder = new HttpHeaderDecoder();
        decoder.listen(headers);
        for (int i = 0; i < httpHeader.length(); i++) {
            decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
        }
        decoder.close();

        assertTrue(headers.containsKey("012345678"));
        assertTrue(headers.containsKey("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(headers.containsKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue(headers.containsKey("!#$%&'*+-.^_`|~"));

        assertEquals(headers.get("012345678"), "A");
        assertEquals(headers.get("abcdefghijklmnopqrstuvwxyz"), "B");
        assertEquals(headers.get("ABCDEFGHIJKLMNOPQRSTUVWXYZ"), "C");
        assertEquals(headers.get("!#$%&'*+-.^_`|~"), "D");
    }


    @Test
    public void testMaxHeaderCount_EqualCount() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderCount = 3;
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }

    @Test
    public void testMaxHeaderCount_EqualCount_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderCount = 3;
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }


    @Test
    public void testMaxHeaderCount_ExceedCount() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderCount = 3;
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }


    @Test
    public void testMaxHeaderCount_ExceedCount_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderCount = 3;
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }

    @Test
    public void testMaxHeaderBytes_Set_NoException() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderBytes = httpHeader.length();
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(maxHeaderBytes, 0);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            e.printStackTrace();
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }

    @Test
    public void testMaxHeaderBytes_Set_NoException_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        int maxHeaderBytes = httpHeader.length();
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(maxHeaderBytes, 0);
        decoder.listen(headers);
        try {
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            e.printStackTrace();
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }

    @Test
    public void testMaxHeaderBytes_Set_Exceed_1Byte_Throw_Exception() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n"; // 5 bytes
        httpHeader += "B:B\r\n"; // 5 bytes
        httpHeader += "C:C\r\n"; // 5 bytes
        httpHeader += "D:D\r\n"; // 5 bytes
        httpHeader += "E:E\r\n"; // 5 bytes
        Map<String, String> headers = new HashMap<>();
        int maxHeaderBytes = httpHeader.length() - 1; // <----
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(maxHeaderBytes, 0);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }


    @Test
    public void testMaxHeaderBytes_Set_Exceed_1Byte_Throw_Exception_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n"; // 5 bytes
        httpHeader += "B:B\r\n"; // 5 bytes
        httpHeader += "C:C\r\n"; // 5 bytes
        httpHeader += "D:D\r\n"; // 5 bytes
        httpHeader += "E:E\r\n"; // 5 bytes
        Map<String, String> headers = new HashMap<>();
        int maxHeaderBytes = httpHeader.length() - 1; // <----
        boolean tooLongException = false;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(maxHeaderBytes, 0);
        try {
            decoder.listen(headers);
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }


    @Test
    public void testMaxHeaderCount_Equal_To_Limit() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        boolean tooLongException = false;
        int maxHeaderCount = 5;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }


    @Test
    public void testMaxHeaderCount_Equal_To_Limit_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        boolean tooLongException = false;
        int maxHeaderCount = 5;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, false);
    }


    @Test
    public void testMaxHeaderCount_Exceed_Limit_Throw_Exception() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        boolean tooLongException = false;
        int maxHeaderCount = 4;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            decoder.decode(httpHeader.getBytes());
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }


    @Test
    public void testMaxHeaderCount_Exceed_Limit_Throw_Exception_SimulateAsync() throws Exception {
        String httpHeader = "";
        httpHeader += "A:A\r\n";
        httpHeader += "B:B\r\n";
        httpHeader += "C:C\r\n";
        httpHeader += "D:D\r\n";
        httpHeader += "E:E\r\n";
        Map<String, String> headers = new HashMap<>();
        boolean tooLongException = false;
        int maxHeaderCount = 4;
        HttpHeaderDecoder decoder = new HttpHeaderDecoder(0, maxHeaderCount);
        try {
            decoder.listen(headers);
            for (int i = 0; i < httpHeader.length(); i++) {
                decoder.decode(new byte[]{(byte) httpHeader.charAt(i)});
            }
            decoder.close();
        } catch (HttpTooLongException e) {
            tooLongException = true;
        }

        assertEquals(tooLongException, true);
    }
}
