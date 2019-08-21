package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.HttpStatus;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServerFileServer {
    private static Map<String, String> DEFAULT_MIME_TYPES = new HashMap<>();

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
            try {
                String contextRoot = "src/test/resource";
                String resourcePath = contextRoot + request.getUri();
                File file = new File(resourcePath);

                if (file.exists()) {
                    if (file.isFile()) {
                        sendFile(request, response, file);
                    } else {
                        sendDirectoryList(request, response, file);
                    }
                } else {
                    response.setStatus(HttpStatus.NOT_FOUND);
                    response.sendFile("src/test/resource/notfound.html");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).handle("/upload", (request, response) -> {
            try {
                uploadFiles(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }


    public static void uploadFiles(HttpRequest request) throws Exception {
        List<HttpForm> forms = request.getHttpForms();
        for (HttpForm form : forms) {
            String uploadFileName = form.getFileName();
            if (uploadFileName != null) {
                form.writeFile("src/test/resource/uploadfiles/" + uploadFileName);
            }
        }
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

    private static void sendFile(HttpRequest request, HttpResponse response, File file) {
        response.addHeader(HttpHeader.CONTENT_TYPE, getContentType(getFileExtension(file)));
        try {
            response.sendFile(file.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendDirectoryList(HttpRequest request, HttpResponse response, File directory) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Directory List</title>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("<h1>" + directory.getName() + "</h1>");
        html.append("<table border='1'>\n");
        html.append("<tr>\n");
        html.append("<th>name</th>");
        html.append("<th>kind</th>");
        html.append("<th>size</th>");
        html.append("</tr>\n");

        String root = request.getUri();
        if (root.equals("/")) {
            root = "";
        }

        // first directories
        for (final File f : directory.listFiles()) {
            if (f.isDirectory()) {
                String name = f.getName();
                html.append("<tr>\n");
                html.append("<td><a href='" + root + "/" + name + "'>[" + name + "]</a></td>\n");
                html.append("<td>dir</td>\n");
                html.append("<td>0</td>\n");
                html.append("</tr>\n");
            }
        }

        // second files
        for (final File f : directory.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                html.append("<tr>\n");
                html.append("<td><a href='" + root + "/" + name + "'>" + name + "</a></td>\n");
                html.append("<td>file</td>\n");
                html.append("<td>" + f.length() + " bytes</td>\n");
                html.append("</tr>\n");
            }
        }

        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        sendHtml(request, response, html.toString());
    }

    private static void sendHtml(HttpRequest request, HttpResponse response, String html) {
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
