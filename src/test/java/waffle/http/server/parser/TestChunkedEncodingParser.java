package waffle.http.server.parser;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;

// curl -X POST --header "Transfer-Encoding: chunked" --data-binary @servlet-4_0_FINAL.pdf "http://localhost"

public class TestChunkedEncodingParser {

    @Test
    public void testBasic() {
        String data = "A\r\n"+
                        "0123456789\r\n"+
                        "6\r\n"+
                        "ABCDEF\r\n"+
                        "0\r\n"+
                        "\r\n"+
                        "HTTP/1.1 200 OK\r\n"+
                        "\r\n";

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        HttpChunkedEncodingParser parser = new HttpChunkedEncodingParser(result);
        try {
            parser.parse(ByteBuffer.wrap(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(result.toString(), "0123456789ABCDEF");
    }


    @Test
    public void testBasicLong() {
        String data = "";
        data = "1\r\n" +
                "A\r\n" +
                "1\r\n" +
                "B\r\n" +
                "1\r\n" +
                "C\r\n" +
                "1\r\n" +
                "D\r\n" +
                "1\r\n" +
                "E\r\n" +
                "1\r\n" +
                "F\r\n" +
                "1\r\n" +
                "G\r\n" +
                "1\r\n" +
                "H\r\n" +
                "1\r\n" +
                "I\r\n" +
                "1\r\n" +
                "J\r\n" +
                "1\r\n" +
                "K\r\n" +
                "1\r\n" +
                "L\r\n" +
                "1\r\n" +
                "M\r\n" +
                "1\r\n" +
                "N\r\n" +
                "1\r\n" +
                "O\r\n" +
                "1\r\n" +
                "P\r\n" +
                "1\r\n" +
                "Q\r\n" +
                "1\r\n" +
                "R\r\n" +
                "1\r\n" +
                "S\r\n" +
                "1\r\n" +
                "T\r\n" +
                "1\r\n" +
                "U\r\n" +
                "1\r\n" +
                "V\r\n" +
                "1\r\n" +
                "W\r\n" +
                "1\r\n" +
                "X\r\n" +
                "1\r\n" +
                "Y\r\n" +
                "1\r\n" +
                "Z\r\n" +
                "0\r\n" +
                "\r\n";

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        HttpChunkedEncodingParser parser = new HttpChunkedEncodingParser(result);
        try {
            parser.parse(ByteBuffer.wrap(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(result.toString(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }



    @Test
    public void testBasicLongWithExtension() {
        String data = "";
        data = "1  ;  index=1\r\n" +
                "A\r\n" +
                "1   ;  index=2\r\n" +
                "B\r\n" +
                "1   ;  index=3\r\n" +
                "C\r\n" +
                "1   ;  index=4\r\n" +
                "D\r\n" +
                "1   ;  index=5\r\n" +
                "E\r\n" +
                "1   ;  index=6\r\n" +
                "F\r\n" +
                "1   ;  index=7\r\n" +
                "G\r\n" +
                "1   ;  index=8\r\n" +
                "H\r\n" +
                "1   ;  index=9\r\n" +
                "I\r\n" +
                "1   ;  index=10\r\n" +
                "J\r\n" +
                "1   ;  index=11\r\n" +
                "K\r\n" +
                "1   ;  index=12\r\n" +
                "L\r\n" +
                "1   ;  index=13\r\n" +
                "M\r\n" +
                "1   ;  index=14\r\n" +
                "N\r\n" +
                "1   ;  index=15\r\n" +
                "O\r\n" +
                "1   ;  index=16\r\n" +
                "P\r\n" +
                "1   ;  index=17\r\n" +
                "Q\r\n" +
                "1   ;  index=18\r\n" +
                "R\r\n" +
                "1   ;  index=19\r\n" +
                "S\r\n" +
                "1   ;  index=20\r\n" +
                "T\r\n" +
                "1   ;  index=21\r\n" +
                "U\r\n" +
                "1   ;  index=22\r\n" +
                "V\r\n" +
                "1   ;  index=23\r\n" +
                "W\r\n" +
                "1   ;  index=24\r\n" +
                "X\r\n" +
                "1   ;  index=25\r\n" +
                "Y\r\n" +
                "1   ;  index=26\r\n" +
                "Z\r\n" +
                "0\r\n" +
                "\r\n";

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        HttpChunkedEncodingParser parser = new HttpChunkedEncodingParser(result);
        try {
            parser.parse(ByteBuffer.wrap(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(result.toString(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }



    @Test
    public void testBasicLongWithExtension_SingleByte() {
        String data = "";
        data = "1  ;  index=1\r\n" +
                "A\r\n" +
                "1   ;  index=2\r\n" +
                "B\r\n" +
                "1   ;  index=3\r\n" +
                "C\r\n" +
                "1   ;  index=4\r\n" +
                "D\r\n" +
                "1   ;  index=5\r\n" +
                "E\r\n" +
                "1   ;  index=6\r\n" +
                "F\r\n" +
                "1   ;  index=7\r\n" +
                "G\r\n" +
                "1   ;  index=8\r\n" +
                "H\r\n" +
                "1   ;  index=9\r\n" +
                "I\r\n" +
                "1   ;  index=10\r\n" +
                "J\r\n" +
                "1   ;  index=11\r\n" +
                "K\r\n" +
                "1   ;  index=12\r\n" +
                "L\r\n" +
                "1   ;  index=13\r\n" +
                "M\r\n" +
                "1   ;  index=14\r\n" +
                "N\r\n" +
                "1   ;  index=15\r\n" +
                "O\r\n" +
                "1   ;  index=16\r\n" +
                "P\r\n" +
                "1   ;  index=17\r\n" +
                "Q\r\n" +
                "1   ;  index=18\r\n" +
                "R\r\n" +
                "1   ;  index=19\r\n" +
                "S\r\n" +
                "1   ;  index=20\r\n" +
                "T\r\n" +
                "1   ;  index=21\r\n" +
                "U\r\n" +
                "1   ;  index=22\r\n" +
                "V\r\n" +
                "1   ;  index=23\r\n" +
                "W\r\n" +
                "1   ;  index=24\r\n" +
                "X\r\n" +
                "1   ;  index=25\r\n" +
                "Y\r\n" +
                "1   ;  index=26\r\n" +
                "Z\r\n" +
                "0\r\n" +
                "\r\n";

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        HttpChunkedEncodingParser parser = new HttpChunkedEncodingParser(result);

        try {
            for (int i = 0; i < data.length(); i++) {
                parser.parse(new byte[] {(byte)data.charAt(i)});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(result.toString(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

}
