package samples;

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

public class HttpServerSendImageFile {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            try {
                response.addHeader(HttpHeader.CONTENT_TYPE, "image/jpg");
                response.sendFile("src/test/resource/images/sulhyun/001.jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }
}
