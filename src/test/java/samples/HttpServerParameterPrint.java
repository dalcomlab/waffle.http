package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;
import java.util.Map;

public class HttpServerParameterPrint {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <title></title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<h1>Request Parameters</h1>\n");
            html.append(requestParameterToHtml(request));
            html.append("</body>\n");
            html.append("</html>\n");
            sendHtml(request, response, html.toString());
        }).start(new ServerOptions());
    }

    public static String requestParameterToHtml(HttpRequest request) {
        Map<String, String[]> parameters = null;
        try {
            parameters = request.getParameterMap();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder html = new StringBuilder();
        html.append("<table border = '1'>\n");
        for (String name : parameters.keySet()) {
            html.append("<tr>\n");
            html.append("<td>" + name + "</td>\n");
            html.append("<td>");
            String[] values = parameters.get(name);
            if (values != null) {
                for (String value : values) {
                    html.append(value);
                    html.append("<br>");
                }
            }
            html.append("</td>\n");
            html.append("</tr>\n");
        }
        html.append("</table>\n");
        return html.toString();
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
