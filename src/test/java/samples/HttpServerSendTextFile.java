package samples;

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

public class HttpServerSendTextFile {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            try {
                response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
                response.sendFile("src/test/resource/hello.html");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }
}
