package com.dalcomlab.sattang.protocol.http.decoder;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class HttpAcceptHeaderDecoderTest {

    private static final Locale EN = Locale.forLanguageTag("en");
    private static final Locale EN_GB = Locale.forLanguageTag("en-gb");
    private static final Locale FR = Locale.forLanguageTag("fr");
    private static final Locale KO_KR = Locale.forLanguageTag("ko_Kr");

    private static final double Q1 = 1.0;
    private static final double Q0_5 = 0.5;
    private static final double Q0_05 = 0.05;
    private static final double Q0_005 = 0.005;

    @Test
    public void testAcceptLanguage_01() throws Exception {
        String header = "en";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }

    @Test
    public void testAcceptLanguage_02() throws Exception {
        String header = "en-gb";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_03() throws Exception {
        String header = "en-gb;";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }



    @Test
    public void testAcceptLanguage_04() throws Exception {
        String header = "en-gb; ";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_05() throws Exception {
        String header = "en-gb;q=1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }

    @Test
    public void testAcceptLanguage_06() throws Exception {
        String header = "en-gb;q=1;";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_07() throws Exception {
        String header = "en-gb; q=1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_08() throws Exception {
        String header = "en-gb; q= 1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_09() throws Exception {
        String header = "en-gb; q = 1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_010() throws Exception {
        String header = "en-gb; q\t\t=\t\t1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }

    @Test
    public void testAcceptLanguage_Multiple_Basic_01() throws Exception {
        String header = "en,fr";

        List<AcceptLanguage> list = new ArrayList();
        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
        assertEquals(list.get(1).getLocale(), FR);
        assertEquals(list.get(1).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptLanguage_Multiple_Basic_02() throws Exception {
        String header = "en,ko_Kr";

        List<AcceptLanguage> list = new ArrayList();
        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
        assertEquals(list.get(1).getLocale(), KO_KR);
        assertEquals(list.get(1).getQuality(), Q1, 0.0001);
    }

    @Test
    public void testAcceptLanguage_Multiple_Basic_03() throws Exception {
        String header = "fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5";

        List<AcceptLanguage> list = new ArrayList();
        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

       System.out.println(list.size());
    }

    @Test
    public void testAcceptLanguage_Multiple_Basic_04() throws Exception {
        String header = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,fr;q=0.6";

        List<AcceptLanguage> list = new ArrayList();
        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 5);
        assertEquals(list.get(0).getLocale(), Locale.forLanguageTag("ko-KR"));
        assertEquals(list.get(0).getQuality(), 1.0, 0.0001);
        assertEquals(list.get(1).getLocale(), Locale.forLanguageTag("ko"));
        assertEquals(list.get(1).getQuality(), 0.9, 0.0001);
        assertEquals(list.get(2).getLocale(), Locale.forLanguageTag("en-US"));
        assertEquals(list.get(2).getQuality(), 0.8, 0.0001);
        assertEquals(list.get(3).getLocale(), Locale.forLanguageTag("en"));
        assertEquals(list.get(3).getQuality(), 0.7, 0.0001);
        assertEquals(list.get(4).getLocale(), Locale.forLanguageTag("fr"));
        assertEquals(list.get(4).getQuality(), 0.6, 0.0001);

    }


    @Test
    public void testMalformed01() throws Exception {
        String header = "en-gb;x=1,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }

    @Test
    public void testMalformed02() throws Exception {
        String header = "en-gb;q=a,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }


    @Test
    public void testMalformed03() throws Exception {
        String header = "en-gb;q=0.5a,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }

    @Test
    public void testMalformed04() throws Exception {
        String header = "n-gb;q=0.05a,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }


    @Test
    public void testMalformed05() throws Exception {
        String header = "n-gb;q=0.005a,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }


    @Test
    public void testMalformed06() throws Exception {
        String header = "en-gb;q=0.00005a,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }


    @Test
    public void testMalformed07() throws Exception {
        String header = "en,,";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testMalformed08() throws Exception {
        String header = ",en,";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testMalformed09() throws Exception {
        String header = ",,en";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN);
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testMalformed10() throws Exception {
        String header = "en;q";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 0);
    }



    @Test
    public void testMalformed11() throws Exception {
        String header = "en-gb;q=1a0";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 0);
    }


    @Test
    public void testMalformed12() throws Exception {
        String header = "en-gb;q=1.a0";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 0);
    }


    @Test
    public void testMalformed13() throws Exception {
        String header = "en-gb;q=1.0a0";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 0);
    }


    @Test
    public void testMalformed14() throws Exception {
        String header = "en-gb;q=1.1";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 0);
    }


    @Test
    public void testMalformed15() throws Exception {
        String header = "en-gb;q=1a0,en-gb;q=0.5";

        List<AcceptLanguage> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((locale, quality) -> list.add(new AcceptLanguage(locale, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getLocale(), EN_GB);
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }



    @Test
    public void testAcceptEncoding_Single() throws Exception {
        String header = "gzip";

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getEncoding(), "gzip");
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
    }

    @Test
    public void testAcceptEncoding_Single_Quality() throws Exception {
        String header = "gzip;q=0.5";

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getEncoding(), "gzip");
        assertEquals(list.get(0).getQuality(), Q0_5, 0.0001);
    }


    @Test
    public void testAcceptEncoding_Multiple_01() throws Exception {
        String header = "deflate,gzip";

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getEncoding(), "deflate");
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);

        assertEquals(list.get(1).getEncoding(), "gzip");
        assertEquals(list.get(1).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptEncoding_Multiple_02() throws Exception {
        String header = "deflate,gzip,*";

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 3);
        assertEquals(list.get(0).getEncoding(), "deflate");
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
        assertEquals(list.get(1).getEncoding(), "gzip");
        assertEquals(list.get(1).getQuality(), Q1, 0.0001);
        assertEquals(list.get(2).getEncoding(), "*");
        assertEquals(list.get(2).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptEncoding_Multiple_03() throws Exception {
        String header = "deflate;q=0.05, gzip;q=0.5, *;q=1.0";

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));
        decoder.decode(ByteBuffer.wrap(header.getBytes()));
        decoder.close();

        assertEquals(list.size(), 3);
        assertEquals(list.get(0).getEncoding(), "deflate");
        assertEquals(list.get(0).getQuality(), Q0_05, 0.0001);
        assertEquals(list.get(1).getEncoding(), "gzip");
        assertEquals(list.get(1).getQuality(), Q0_5, 0.0001);
        assertEquals(list.get(2).getEncoding(), "*");
        assertEquals(list.get(2).getQuality(), Q1, 0.0001);
    }


    @Test
    public void testAcceptEncoding_Multiple_04() throws Exception {

        List<AcceptEncoding> list = new ArrayList();

        HttpAcceptHeaderDecoder decoder = new HttpAcceptHeaderDecoder();
        decoder.listen((encoding, quality) -> list.add(new AcceptEncoding(encoding, quality)));

        decoder.decode(ByteBuffer.wrap("gzip".getBytes()));
        decoder.close();

        decoder.decode(ByteBuffer.wrap("compress".getBytes()));
        decoder.close();

        decoder.decode(ByteBuffer.wrap("deflate".getBytes()));
        decoder.close();

        decoder.decode(ByteBuffer.wrap("br".getBytes()));
        decoder.close();

        decoder.decode(ByteBuffer.wrap("identity".getBytes()));
        decoder.close();

        decoder.decode(ByteBuffer.wrap("*".getBytes()));
        decoder.close();

        assertEquals(list.size(), 6);
        assertEquals(list.get(0).getEncoding(), "gzip");
        assertEquals(list.get(0).getQuality(), Q1, 0.0001);
        assertEquals(list.get(1).getEncoding(), "compress");
        assertEquals(list.get(1).getQuality(), Q1, 0.0001);
        assertEquals(list.get(2).getEncoding(), "deflate");
        assertEquals(list.get(2).getQuality(), Q1, 0.0001);
        assertEquals(list.get(3).getEncoding(), "br");
        assertEquals(list.get(3).getQuality(), Q1, 0.0001);
        assertEquals(list.get(4).getEncoding(), "identity");
        assertEquals(list.get(4).getQuality(), Q1, 0.0001);
        assertEquals(list.get(5).getEncoding(), "*");
        assertEquals(list.get(5).getQuality(), Q1, 0.0001);
    }




    /**
     *
     */
    private class AcceptLanguage {
        private Locale locale;
        private double quality;
        public AcceptLanguage(final String locale, final double quality) {
            this.locale = Locale.forLanguageTag(locale);
            this.quality = quality;
        }

        public Locale getLocale() {
            return locale;
        }

        public double getQuality() {
            return quality;
        }
    }


    /**
     *
     */
    private class AcceptEncoding {
        private String encoding;
        private double quality;
        public AcceptEncoding(final String encoding, final double quality) {
            this.encoding = encoding;
            this.quality = quality;
        }

        public String getEncoding() {
            return encoding;
        }

        public double getQuality() {
            return quality;
        }
    }
}
