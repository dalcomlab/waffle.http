package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;
import java.util.List;

public class HttpServerFileUpload {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <title></title>\n");
            html.append("</head>\n");
            html.append("<form action='http://localhost:8080/upload' id='frm' method='post' enctype='multipart/form-data'>\n");
            html.append("<input type='file' name='file01'>\n");
            html.append("<input type='submit' value='upload'>\n");
            html.append("</form>\n");
            html.append("<body>\n");
            html.append("</body>\n");
            html.append("</html>\n");
            sendHtml(request, response, html.toString());
        }).handle("/upload", (request, response) -> {
            try {
                uploadFiles(request, "src/test/resource/uploadfiles");
                String html = uploadFilesToHtml(request);
                sendHtml(request, response, html);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }

    public static void uploadFiles(HttpRequest request, String uploadPath) throws Exception {
        List<HttpForm> forms = request.getHttpForms();
        for (HttpForm form : forms) {
            String uploadFileName = form.getFileName();
            if (uploadFileName != null) {
                // 지정한 폴더에 업로드한 파일을 저장한다.
                form.writeFile(uploadPath + "/" + uploadFileName);
            }
        }
    }

    public static String uploadFilesToHtml(HttpRequest request) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title></title>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>Upload Files</h1>\n");
        html.append("<table border = '1'>\n");
        html.append("<tr>\n");
        html.append("<td>name</td>");
        html.append("<td>content-disposition</td>");
        html.append("<td>file name</td>");
        html.append("</tr>\n");

        List<HttpForm> forms = request.getHttpForms();
        for (HttpForm form : forms) {
            html.append("<tr>\n");
            html.append("<td>" + form.getName() + "</td>");
            html.append("<td>" + form.getContentDisposition() + "</td>");
            html.append("<td>" + form.getFileName() + "</td>");
            html.append("</tr>\n");
        }
        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>\n");
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
