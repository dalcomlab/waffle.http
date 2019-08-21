package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.HttpMethod;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;

/**
 * public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
 * public static final HttpMethod GET = new HttpMethod("GET");
 * public static final HttpMethod HEAD = new HttpMethod("HEAD");
 * public static final HttpMethod POST = new HttpMethod("POST");
 * public static final HttpMethod PUT = new HttpMethod("PUT");
 * public static final HttpMethod DELETE = new HttpMethod("DELETE");
 * public static final HttpMethod TRACE = new HttpMethod("TRACE");
 * public static final HttpMethod PROPFIND = new HttpMethod("PROPFIND");
 * public static final HttpMethod PROPPATCH = new HttpMethod("PROPPATCH");
 * public static final HttpMethod MKCOL = new HttpMethod("MKCOL");
 * public static final HttpMethod COPY = new HttpMethod("COPY");
 * public static final HttpMethod MOVE = new HttpMethod("MOVE");
 * public static final HttpMethod LOCK = new HttpMethod("LOCK");
 * public static final HttpMethod UNLOCK = new HttpMethod("UNLOCK");
 * public static final HttpMethod ACL = new HttpMethod("ACL");
 * public static final HttpMethod REPORT = new HttpMethod("REPORT");
 * public static final HttpMethod VERSION_CONTROL = new HttpMethod("VERSION-CONTROL");
 * public static final HttpMethod CHECKIN = new HttpMethod("CHECKIN");
 * public static final HttpMethod CHECKOUT = new HttpMethod("CHECKOUT");
 * public static final HttpMethod SEARCH = new HttpMethod("SEARCH");
 * public static final HttpMethod MKWORKSPACE = new HttpMethod("MKWORKSPACE");
 * public static final HttpMethod UPDATE = new HttpMethod("UPDATE");
 * public static final HttpMethod LABEL = new HttpMethod("LABEL");
 * public static final HttpMethod MERGE = new HttpMethod("MERGE");
 * public static final HttpMethod BASELINE_CONTROL = new HttpMethod("BASELINE-CONTROL");
 * public static final HttpMethod MKACTIVITY = new HttpMethod("MKACTIVITY");
 * public static final HttpMethod PATCH = new HttpMethod("PATCH");
 * public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
 */
public class HttpServerMethod {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {

            HttpMethod method = request.getMethod();
            if (method == HttpMethod.GET) {
                doGet(request, response);
            } else if (method == HttpMethod.POST) {
                doPost(request, response);
            } else if (method == HttpMethod.PUT) {
                doPut(request, response);
            }
        }).start(new ServerOptions());
    }

    public static void doGet(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>GET</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>GET Method!!!</h1>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
    }

    public static void doPost(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>POST</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>POST Method!!!</h1>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
    }

    public static void doPut(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>PUT</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>PUT Method!!!</h1>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
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
