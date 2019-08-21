package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;

public class HttpServerRouting {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            sendHtml(request, response, "default");
        }).handle("/red", (request, response) -> {
            String html = "<h1 style='color:red'>red</h1>";
            sendHtml(request, response, html);
        }).handle("/blue", (request, response) -> {
            String html = "<h1 style='color:blue'>blue</h1>";
            sendHtml(request, response, html);
        }).handle("/green", (request, response) -> {
            String html = "<h1 style='color:green'>green</h1>";
            sendHtml(request, response, html);
        }).start(new ServerOptions());
    }

    public static void sendHtml(HttpRequest request, HttpResponse response, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("    <title></title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append(content);
        sb.append("</body>\n");
        sb.append("</html>\n");
        String html = sb.toString();
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
