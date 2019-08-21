# 1. 소개

자바 NIO 기반의 웹프레임워크. 아래 프로토콜을 지원할 예정임.

| 프로토콜  | 지원 여부 |
|---|---|
| HTTP/1.1  |◯   |
| HTTP/2.0  |◯   |
| AJP 1.3  | ◯  |
| WebSocket  | ◯  |

# 2. 사용
* Maven 을 사용하는 경우 내부 메이븐 저장소를 사용한다.
```xml
<servers>
    <server>
        <id>project-private-repo</id>
        <username>cliprnd</username>
        <password>clipsoft1644</password>
    </server>
</servers>
```


```xml
<dependencies>
        <dependency>
            <groupId>com.dalcomlab.sattang</groupId>
            <artifactId>com.dalcomlab.sattang</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    
    <repositories>
        <repository>
            <id>dalcomlab-private-repo</id>
            <url>http://52.231.78.123:18081/repository/maven-releases</url>
        </repository>
    </repositories>
```

* Release 에서 `com.dalcomlab.sattang-1.0.0.jar` 파일을 다운로드 받아 참조한다.

# 3. 참조
 ## 3.1 AJP
  * https://tomcat.apache.org/connectors-doc/ajp/ajpv13a.html
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-1-729a20c359e4
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-2-d9d4854d3985
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-3-e4dc7e1eda19
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-4-361afb28b87b
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-5-8945a909c754
  * https://medium.com/@dalcomlab/ajp-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-6-5031ecc1884

# 4. 샘플
아래 기술한 모든 샘플은 src/test/java/samples 에서 확인할 수 있다.

## HTTP - Hello World #1

```java
import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHandler;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;

public class HttpServerHelloWorld {

    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer("0.0.0.0", 8080);
        server.handle("/", new HttpHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response) {
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
            }
        });

        server.start(new ServerOptions());
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

``` 


## HTTP - Hello World #2
자바의 람다를 사용하면 아래처럼 코드를 간략하게 작성할 수 있다. 특별한 경우가 아니면 앞으로 모든 샘플은 람다로 작성함.
```java
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

``` 

## HTTP - URL routing
```code 
http://localhost:8080/context/path/path/
                     ~~~~~~~~~
                    context path
```
* 전체 URL 경로에서 포트 이후에 나오는 첫번째 경로를 컨텍스트 경로라고 한다. 
* 컨텍스트 경로에 따라 다른 핸들러를 적용할 수 있다. 컨텍스트에 매칭되는 핸들러가 없으면 디폴트 ("/")에  설정한 핸들러를 호출한다.

```java
package samples;

import com.dalcomlab.com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.com.dalcomlab.sattang.server.ServerOptions;

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
```

## HTTP - METHOD routing
```java
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

```

## HTTP - Send file #1
* `HttpResponse.sendFile` 메서드로 정적 파일을 보낼 수 있다.
* 파일 타입에 따라 적당한 Content-Type 을 설정해야 브라우저에서 제대로 볼 수 있다.
```java
package samples;

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

public class HttpServerSendTextFile {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            try {
                response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
                response.sendFile("/hello.html");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }
}
```

## HTTP - Send file #2
```java
package samples;

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

public class HttpServerSendImageFile {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            try {
                response.addHeader(HttpHeader.CONTENT_TYPE, "image/jpg");
                response.sendFile("/Users/woman.jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(new ServerOptions());
    }
}

```

## HTTP - Print HTTP request headers
```java
package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;
import java.util.Set;

public class HttpServerHeaderPrint {
    public static void main(String[] args) throws Exception {
        new HttpServer("0.0.0.0", 8080).handle("/", (request, response) -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <title></title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<h1>Request Headers</h1>\n");
            html.append(requestHeaderToHtml(request));
            html.append("</body>\n");
            html.append("</html>\n");
            sendHtml(request, response, html.toString());
        }).start(new ServerOptions());
    }

    public static String requestHeaderToHtml(HttpRequest request) {
        StringBuilder html = new StringBuilder();
        html.append("<table border = '1'>\n");
        Set<String> names = request.getHeaderNames();
        for (String name : names) {
            html.append("<tr>\n");
            html.append("<td>" + name + "</td>\n");
            html.append("<td>" + request.getHeader(name) + "</td>\n");
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

```
## HTTP - Print HTTP request parameters(QueryString & POST)
```java
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

```
## HTTP - File upload
```java
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


```

## Simple HTTP server
```java
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

``` 

## HTTP - Session
```java
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

```

## AJP - Hello World
* 아파치에 8009 포트로 AJP 설정이 되어야 테스트가 가능함.
```java
package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.AjpServer;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;

public class AJPServerHelloWorld {
    public static void main(String[] args) throws Exception {
        new AjpServer("0.0.0.0", 8009).handle("/", (request, response) -> {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("    <title>Hello World!</title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<h1>AJP - Hello World!</h1>\n");
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

```