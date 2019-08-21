package com.dalcomlab.sattang.protocol.http.decoder;

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;
import com.dalcomlab.sattang.protocol.http.form.HttpFormFile;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class HttpMultiPartFormDecoderTest {

    public HttpMultiPartFormDecoder createDecoder(byte[] boundary) {
        HttpMultiPartFormDecoder decoder = new HttpMultiPartFormDecoder(boundary);
        decoder.setFormBuilder((header -> new MockHttpPart(header)));
        return decoder;
    }

    @Test
    public void testJustOnePart() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data;name=file1\r\n" +
                "\r\n" +
                "A\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());
        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();


        assertEquals(parts.size(), 1);
        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(0).toString(), "A");

    }

    @Test
    public void testContainsEncodedName() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data;name=\"=?utf-8?B?7YyM7J28?=\"\r\n" +
                "\r\n" +
                "A\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();


        assertEquals(parts.size(), 1);
        assertEquals(parts.get(0).getName(), "파일");
        assertEquals(parts.get(0).toString(), "A");

    }


    @Test
    public void testBodyContainsPartialBoundaryAndBreak() throws Exception {
        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode("--BOUNDARY\r\n".getBytes());
        decoder.decode("Content-Disposition:form-data;name=file1\r\n".getBytes());
        decoder.decode("\r\n--".getBytes());
        decoder.decode("A1".getBytes());
        decoder.decode("\r\n--B".getBytes());
        decoder.decode("B1".getBytes());
        decoder.decode("\r\n--BOU".getBytes());
        decoder.decode("C1".getBytes());
        decoder.decode("\r\n".getBytes());
        decoder.decode("--BOUNDARY--\r\n".getBytes());
        decoder.close();


        assertEquals(parts.size(), 1);

        assertEquals(parts.get(0).getName(), "file1");

        assertEquals(parts.get(0).toString(), "--A1\r\n--BB1\r\n--BOUC1");

    }


    @Test
    public void testNoBoundary() throws Exception {
        String data = "--\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3\r\n" +
                "----\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();


        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3");

    }


    @Test
    public void testBodyIsEmpty() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "");
        assertEquals(parts.get(1).toString(), "");
        assertEquals(parts.get(2).toString(), "");
    }


    @Test
    public void testBodyIsLinearWhiteSpace_00() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\r" + // data
                "\n\n" + // data
                "\r\n" + // data
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "\r\r" + // data
                "\n\n" + // data
                "\r\n" + // data
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "\r\r" + // data
                "\n\n" + // data
                "\r\n" + // data
                "\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "\r\r\n\n\r\n");
        assertEquals(parts.get(1).toString(), "\r\r\n\n\r\n");
        assertEquals(parts.get(2).toString(), "\r\r\n\n\r\n");
    }


    @Test
    public void testBodyIsLinearWhiteSpace_01() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\r\r\r" + // data
                "\n\n\n\n" + // data
                "\t\t\t\t" + // data
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "\r\r" + // data
                "\n\n" + // data
                "\r\n" + // data
                "\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "\n" +   // data
                "\r" +   // data
                "\r\n" + // data
                "\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "\r\r\r\r\n\n\n\n\t\t\t\t");
        assertEquals(parts.get(1).toString(), "\r\r\n\n\r\n");
        assertEquals(parts.get(2).toString(), "\n\r\r\n");
    }

    @Test
    public void testBodyStartWithBoundary() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "--BOUNDARY--A1\r\n" +
                "--BOUNDARY--A2\r\n" +
                "--BOUNDARY--A3\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "--BOUNDARY--B1\r\n" +
                "--BOUNDARY--B2\r\n" +
                "--BOUNDARY--B3\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 2);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");

        assertEquals(parts.get(0).toString(), "--BOUNDARY--A1\r\n--BOUNDARY--A2\r\n--BOUNDARY--A3");
        assertEquals(parts.get(1).toString(), "--BOUNDARY--B1\r\n--BOUNDARY--B2\r\n--BOUNDARY--B3");

    }


    @Test
    public void testBodyIncludeBoundary() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1--BOUNDARY--A1\r\n" +
                "A2--BOUNDARY--A2\r\n" +
                "A3--BOUNDARY--A3\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "B1--BOUNDARY--B1\r\n" +
                "B2--BOUNDARY--B2\r\n" +
                "B3--BOUNDARY--B3\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 2);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");


        assertEquals(parts.get(0).toString(), "A1--BOUNDARY--A1\r\nA2--BOUNDARY--A2\r\nA3--BOUNDARY--A3");
        assertEquals(parts.get(1).toString(), "B1--BOUNDARY--B1\r\nB2--BOUNDARY--B2\r\nB3--BOUNDARY--B3");

    }


    @Test
    public void testBodyEndWidthBoundary() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1--BOUNDARY\r\n" +
                "A2--BOUNDARY\r\n" +
                "A3--BOUNDARY\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "B1--BOUNDARY\r\n" +
                "B2--BOUNDARY\r\n" +
                "B3--BOUNDARY\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 2);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");

        assertEquals(parts.get(0).toString(), "A1--BOUNDARY\r\nA2--BOUNDARY\r\nA3--BOUNDARY");
        assertEquals(parts.get(1).toString(), "B1--BOUNDARY\r\nB2--BOUNDARY\r\nB3--BOUNDARY");

    }


    @Test
    public void testBodyIncludePartialBoundary() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "-\r\n" +
                "--\r\n" +
                "--B\r\n" +
                "--BO\r\n" +
                "--BOU\r\n" +
                "--BOUN\r\n" +
                "--BOUND\r\n" +
                "--BOUNDA\r\n" +
                "--BOUNDAR\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "-\r\n" +
                "--\r\n" +
                "--B\r\n" +
                "--BO\r\n" +
                "--BOU\r\n" +
                "--BOUN\r\n" +
                "--BOUND\r\n" +
                "--BOUNDA\r\n" +
                "--BOUNDAR\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 2);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");

        assertEquals(parts.get(0).toString(), "-\r\n--\r\n--B\r\n--BO\r\n--BOU\r\n--BOUN\r\n--BOUND\r\n--BOUNDA\r\n--BOUNDAR");
        assertEquals(parts.get(1).toString(), "-\r\n--\r\n--B\r\n--BO\r\n--BOU\r\n--BOUN\r\n--BOUND\r\n--BOUNDA\r\n--BOUNDAR");

    }


    @Test
    public void testBodyIncludePartialBoundary2() throws Exception {
        String data = "------\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\n-a\r\n--b\r\n---c\r\n----d-------------------------------------\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "---------------------------------------------\r\n" +
                "--------\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("----".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

    }

    @Test
    public void testBodyIncludePartialBoundary3() throws Exception {
        String data = "--abcd\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\n-a\r\n--b\r\n---c\r\n----d-------------------------------------\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "---------------------------------------------\r\n" +
                "--abcd--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("abcd".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

    }

    @Test
    public void testBodyIncludePartialBoundary4() throws Exception {
        String data = "--ABABAB--\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "\r\n-a\r\n--b\r\n---c\r\n" +
                "--ABABAB--\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "---------------------------------------------\r\n" +
                "--ABABAB----\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("ABABAB--".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

    }


    @Test
    public void testBasic() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3");

    }


    @Test
    public void testBasic_PartialBytes_00() throws Exception {

        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);

        // first part
        decoder.decode("--".getBytes());
        decoder.decode("BOUNDARY".getBytes());
        decoder.decode("\r\n".getBytes());
        decoder.decode("Content-Disposition:form-data; name=\"file1\"\r\n".getBytes());
        decoder.decode("\r".getBytes());
        decoder.decode("\n".getBytes());
        decoder.decode("A1\r\n".getBytes());

        // second part
        decoder.decode("--BOUND".getBytes());
        decoder.decode("ARY\r".getBytes());
        decoder.decode("\n".getBytes());
        decoder.decode("Content-Disposition:form-data; name=\"file2\"\r\n".getBytes());
        decoder.decode("\r\n".getBytes());
        decoder.decode("A2\r\n".getBytes());

        // third part
        decoder.decode("--BOUNDARY\r\n".getBytes());
        decoder.decode("Content-Disposition:form-data; name=\"file3\"\r\n".getBytes());
        decoder.decode("\r\n".getBytes());
        decoder.decode("A3\r\n".getBytes());
        decoder.decode("--BOUNDARY--\r\n".getBytes());

        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3");

    }

    @Test
    public void testBasic_PartialBytes_01() throws Exception {
        String data1 = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n--";

        String data2 = "BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY\r\n";
        String data3 = "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3\r\n" +
                "--BOUNDARY--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data1.getBytes());
        decoder.decode(data2.getBytes());
        decoder.decode(data3.getBytes());
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3");

    }

    @Test
    public void testBasic_SingleByte() throws Exception {
        String data = "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3\r\n" +
                "--BOUNDARY--\r\n";

        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);

        for (int i = 0; i < data.length(); i++) {
            decoder.decode(new byte[]{(byte) data.charAt(i)});
        }
        decoder.close();

        assertEquals(parts.size(), 3);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3");
    }


    @Test
    public void testOneLevelMultipartMixed() throws Exception {
        String data = "--BOUNDARY_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY_A--\r\n" +
                "\r\n" +
                "--BOUNDARY_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "B1\r\n" +
                "--BOUNDARY_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file4\"\r\n" +
                "\r\n" +
                "B2\r\n" +
                "--BOUNDARY_B--\r\n" +
                "\r\n" +
                "--BOUNDARY_S--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY_S".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 4);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");
        assertEquals(parts.get(3).getName(), "file4");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "B1");
        assertEquals(parts.get(3).toString(), "B2");
    }

    @Test
    public void testOneLevelMultipartMixed_SingleByte() throws Exception {
        String data = "--BOUNDARY_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY_A--\r\n" +
                "\r\n" +
                "--BOUNDARY_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "B1\r\n" +
                "--BOUNDARY_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file4\"\r\n" +
                "\r\n" +
                "B2\r\n" +
                "--BOUNDARY_B--\r\n" +
                "\r\n" +
                "--BOUNDARY_S--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY_S".getBytes());

        decoder.listen(parts);
        for (int i = 0; i < data.length(); i++) {
            decoder.decode(new byte[]{(byte) data.charAt(i)});
        }
        decoder.close();

        assertEquals(parts.size(), 4);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");
        assertEquals(parts.get(3).getName(), "file4");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "B1");
        assertEquals(parts.get(3).toString(), "B2");
    }


    @Test
    public void testTwoLevelMultipartMixed() throws Exception {
        String data = "--BOUNDARY_S_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A_S\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A_A\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3-1\r\n" +
                "--BOUNDARY_A_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file4\"\r\n" +
                "\r\n" +
                "A3-2\r\n" +
                "--BOUNDARY_A_A--\r\n" +
                "\r\n" +
                "--BOUNDARY_A_S--\r\n" +
                "\r\n" +
                "--BOUNDARY_S_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B_S\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file5\"\r\n" +
                "\r\n" +
                "B1\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file6\"\r\n" +
                "\r\n" +
                "B2\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B_B\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file7\"\r\n" +
                "\r\n" +
                "B3-1\r\n" +
                "--BOUNDARY_B_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file8\"\r\n" +
                "\r\n" +
                "B3-2\r\n" +
                "--BOUNDARY_B_B--\r\n" +
                "\r\n" +
                "--BOUNDARY_B_S--\r\n" +
                "\r\n" +
                "--BOUNDARY_S_S--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY_S_S".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();

        assertEquals(parts.size(), 8);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");
        assertEquals(parts.get(3).getName(), "file4");
        assertEquals(parts.get(4).getName(), "file5");
        assertEquals(parts.get(5).getName(), "file6");
        assertEquals(parts.get(6).getName(), "file7");
        assertEquals(parts.get(7).getName(), "file8");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3-1");
        assertEquals(parts.get(3).toString(), "A3-2");
        assertEquals(parts.get(4).toString(), "B1");
        assertEquals(parts.get(5).toString(), "B2");
        assertEquals(parts.get(6).toString(), "B3-1");
        assertEquals(parts.get(7).toString(), "B3-2");
    }


    @Test
    public void testTwoLevelMultipartMixed_SingleByte() throws Exception {
        String data = "--BOUNDARY_S_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A_S\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file1\"\r\n" +
                "\r\n" +
                "A1\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file2\"\r\n" +
                "\r\n" +
                "A2\r\n" +
                "--BOUNDARY_A_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_A_A\"\r\n" +
                "\r\n" +
                "--BOUNDARY_A_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file3\"\r\n" +
                "\r\n" +
                "A3-1\r\n" +
                "--BOUNDARY_A_A\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file4\"\r\n" +
                "\r\n" +
                "A3-2\r\n" +
                "--BOUNDARY_A_A--\r\n" +
                "\r\n" +
                "--BOUNDARY_A_S--\r\n" +
                "\r\n" +
                "--BOUNDARY_S_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B_S\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file5\"\r\n" +
                "\r\n" +
                "B1\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file6\"\r\n" +
                "\r\n" +
                "B2\r\n" +
                "--BOUNDARY_B_S\r\n" +
                "Content-Type: multipart/mixed; boundary=\"BOUNDARY_B_B\"\r\n" +
                "\r\n" +
                "--BOUNDARY_B_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file7\"\r\n" +
                "\r\n" +
                "B3-1\r\n" +
                "--BOUNDARY_B_B\r\n" +
                "Content-Type: text/plain;charset=\"utf-8\"\r\n" +
                "Content-Disposition:form-data; name=\"file8\"\r\n" +
                "\r\n" +
                "B3-2\r\n" +
                "--BOUNDARY_B_B--\r\n" +
                "\r\n" +
                "--BOUNDARY_B_S--\r\n" +
                "\r\n" +
                "--BOUNDARY_S_S--\r\n";


        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY_S_S".getBytes());

        decoder.listen(parts);
        for (int i = 0; i < data.length(); i++) {
            decoder.decode(new byte[]{(byte) data.charAt(i)});
        }
        decoder.close();

        assertEquals(parts.size(), 8);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(1).getName(), "file2");
        assertEquals(parts.get(2).getName(), "file3");
        assertEquals(parts.get(3).getName(), "file4");
        assertEquals(parts.get(4).getName(), "file5");
        assertEquals(parts.get(5).getName(), "file6");
        assertEquals(parts.get(6).getName(), "file7");
        assertEquals(parts.get(7).getName(), "file8");

        assertEquals(parts.get(0).toString(), "A1");
        assertEquals(parts.get(1).toString(), "A2");
        assertEquals(parts.get(2).toString(), "A3-1");
        assertEquals(parts.get(3).toString(), "A3-2");
        assertEquals(parts.get(4).toString(), "B1");
        assertEquals(parts.get(5).toString(), "B2");
        assertEquals(parts.get(6).toString(), "B3-1");
        assertEquals(parts.get(7).toString(), "B3-2");
    }


    @Test
    public void test10KBData() throws Exception {

        String samples = createSample(1024 * 10);
        String data = "--BOUNDARY\r\n";
        data += "Content-Disposition:form-data; name=\"file1\"\r\n";
        data += "\r\n";
        data += samples;
        data += "\r\n";
        data += "--BOUNDARY--\r\n";

        ArrayList<HttpForm> parts = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = createDecoder("BOUNDARY".getBytes());

        decoder.listen(parts);
        decoder.decode(data.getBytes());
        decoder.close();


        assertEquals(parts.size(), 1);

        assertEquals(parts.get(0).getName(), "file1");
        assertEquals(parts.get(0).toString(), samples);

    }

    public String createSample(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("A");
        }
        return sb.toString();
    }

    /**
     *
     */
    private class MockHttpPart extends HttpFormFile {
        private ByteArrayOutputStream output = new ByteArrayOutputStream();

        public MockHttpPart(final HttpHeader header) {
            super(header);
        }

        /**
         * @return
         */
        public OutputStream getOutputStream() {
            return output;
        }

        public String toString() {
            return output.toString();
        }
    }

}
