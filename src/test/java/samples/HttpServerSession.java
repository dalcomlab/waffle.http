package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.HttpStatus;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpServerSession {
    private static Map<String, String> DEFAULT_MIME_TYPES = new HashMap<>();
    private static String ID = "dalcom";
    private static String PASSWORD = "sattang";

    static {
        DEFAULT_MIME_TYPES.put("bmp", "image/bmp");
        DEFAULT_MIME_TYPES.put("gif", "image/gif");
        DEFAULT_MIME_TYPES.put("htm", "text/html");
        DEFAULT_MIME_TYPES.put("html", "text/html");
        DEFAULT_MIME_TYPES.put("jpeg", "image/jpeg");
        DEFAULT_MIME_TYPES.put("jpg", "image/jpeg");
        DEFAULT_MIME_TYPES.put("js", "application/js");
        DEFAULT_MIME_TYPES.put("json", "application/json");
        DEFAULT_MIME_TYPES.put("png", "image/png");
        DEFAULT_MIME_TYPES.put("xml", "application/xml");
        DEFAULT_MIME_TYPES.put("zip", "application/zip");
        DEFAULT_MIME_TYPES.put("css", "text/css");
        DEFAULT_MIME_TYPES.put("exe", "application/octet-stream");
        DEFAULT_MIME_TYPES.put("pdf", "application/pdf");
        DEFAULT_MIME_TYPES.put("ico", "image/x-icon\n");
    }

    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            String jsession = getJSessionId(request);
            if (jsession == null) {
                redirect(request, response, "/login");
                return;
            }
            try {
                String contextRoot = "src/test/resource";
                String resourcePath = contextRoot + request.getUri();
                File file = new File(resourcePath);

                if (file.exists() && file.isFile()) {
                    sendFile(request, response, file);
                } else {
                    response.setStatus(HttpStatus.NOT_FOUND);
                    response.sendFile("src/test/resource/notfound.html");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).handle("/login", (request, response) -> {
            sendLoginPage(request, response);
        }).handle("/auth", (request, response) -> {
            sendAuthPage(request, response);
        }).start(new ServerOptions());
    }

    private static void redirect(HttpRequest request, HttpResponse response, String redirectUrl) {
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT);
        response.addHeader(HttpHeader.LOCATION, redirectUrl);
        try {
            response.getOutputStream().write(" ".getBytes());
        } catch (Exception e) {

        }
    }

    private static void sendFile(HttpRequest request, HttpResponse response, File file) {
        response.setStatus(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, getContentType(getFileExtension(file)));
        try {
            response.sendFile(file.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLoginPage(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Login page</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<form action='/auth' method='POST'>\n");
        html.append("id : <input type='text' name='id' value='dalcom'/>\n");
        html.append("password : <input type='password' name='password' value='sattang'/>\n");
        html.append("<input type='submit' value='login'/>\n");
        html.append("<form>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
    }


    public static void sendAuthPage(HttpRequest request, HttpResponse response) {
        String id = null;
        String password = null;
        try {
            id = request.getParameter("id");
            password = request.getParameter("password");
            if (id == null || password == null) {
                redirect(request, response, "/login");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 인증되면 JSESSIONID 을 설정한다.
        if (id.equalsIgnoreCase(ID) && password.equalsIgnoreCase(PASSWORD)) {
            response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=123456789");
            sendWelcomePage(request, response);
        } else {
            sendErrorPage(request, response);
        }
    }

    public static void sendErrorPage(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Error</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>Error</h1>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
    }

    public static void sendWelcomePage(HttpRequest request, HttpResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Welcome</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>Welcome</h1>\n");
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

    private static String getJSessionId(HttpRequest request) {
        String cookie = request.getHeader(HttpHeader.COOKIE);
        if (cookie == null) {
            return null;
        }
        return "";
    }

    private static String getContentType(String type) {
        if (DEFAULT_MIME_TYPES.containsKey(type)) {
            return DEFAULT_MIME_TYPES.get(type);
        }
        return "text/plain";
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1);
    }
}
