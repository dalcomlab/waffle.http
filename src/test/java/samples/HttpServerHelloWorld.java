package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;

public class HttpServerHelloWorld {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <title>Hello World!</title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<h1>Hello World!</h1>\n");
            html.append("</body>\n");
            html.append("</html>\n");
            sendHtml(request, response, html.toString());
        }).start(new ServerOptions());
    }


    public static void sendHtml(HttpRequest request, HttpResponse response, String html) {
        try {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.addHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(html.getBytes().length));
            OutputStream output = response.getOutputStream();
            if (output != null) {
                output.write(html.getBytes());
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
